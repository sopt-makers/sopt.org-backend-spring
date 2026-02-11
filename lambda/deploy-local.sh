#!/bin/bash

# ============================================
# SOPT.org Spring Lambda 로컬 배포 스크립트
# 사용법: ./lambda/deploy-local.sh [dev|prod]
# ============================================

set -e  # 에러 발생 시 중단

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 환경 설정
ENV=${1:-dev}
S3_BUCKET="sopt-org-lambda-deploy"
STACK_NAME="sopt-org-spring-${ENV}"
AWS_REGION="ap-northeast-2"

# ✨ AWS 프로필 설정 (배포용)
export AWS_PROFILE=team-org

# 스크립트 디렉토리로 이동
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo -e "${GREEN}🚀 SOPT.org Spring Lambda 배포 시작 (환경: ${ENV})${NC}"
echo "============================================"

# ============================================
# 0. 환경변수 파일 로드
# ============================================
ENV_FILE="${SCRIPT_DIR}/.env"
if [ ! -f "$ENV_FILE" ]; then
    echo -e "${RED}❌ 환경변수 파일이 없습니다: ${ENV_FILE}${NC}"
    echo -e "${YELLOW}💡 lambda/.env.example을 참고하여 lambda/.env 파일을 생성하세요.${NC}"
    echo "   cp lambda/.env.example lambda/.env"
    exit 1
fi

echo -e "${YELLOW}📄 환경변수 로드 중: ${ENV_FILE}${NC}"
set -a  # export all variables
source "$ENV_FILE"
set +a

# ✨ Lambda용 AWS 키를 별도 변수에 저장
LAMBDA_AWS_ACCESS_KEY_ID="${AWS_ACCESS_KEY_ID}"
LAMBDA_AWS_SECRET_ACCESS_KEY="${AWS_SECRET_ACCESS_KEY}"

# ✨ 배포용으로 AWS 환경변수 해제 (프로필 사용하도록)
unset AWS_ACCESS_KEY_ID
unset AWS_SECRET_ACCESS_KEY

# 필수 환경변수 확인
if [ -z "$DB_HOST" ] || [ -z "$DB_PASSWORD" ]; then
    echo -e "${RED}❌ 필수 환경변수가 설정되지 않았습니다.${NC}"
    echo "   DB_HOST, DB_PASSWORD 등을 확인하세요."
    exit 1
fi

echo -e "${GREEN}✅ 환경변수 로드 완료${NC}"
echo -e "${GREEN}✅ 배포용 AWS 프로필: ${AWS_PROFILE}${NC}"

# ============================================
# 1. Lambda JAR 빌드
# ============================================
echo -e "${YELLOW}📦 Step 1: Lambda JAR 빌드 중...${NC}"
cd "$PROJECT_ROOT"
./gradlew clean lambdaJar -x test -x sentryBundleSourcesJava

# 빌드된 파일 확인
JAR_FILE=$(ls build/distributions/*-lambda.zip 2>/dev/null | head -1)
if [ -z "$JAR_FILE" ]; then
    echo -e "${RED}❌ Lambda ZIP 파일을 찾을 수 없습니다.${NC}"
    exit 1
fi
echo -e "${GREEN}✅ 빌드 완료: ${JAR_FILE}${NC}"

# ============================================
# 2. S3 업로드
# ============================================
echo -e "${YELLOW}☁️ Step 2: S3 업로드 중...${NC}"
TIMESTAMP=$(date +"%Y%m%d-%H%M%S")
S3_KEY="lambda/${STACK_NAME}-${TIMESTAMP}.zip"

aws s3 cp "$JAR_FILE" "s3://${S3_BUCKET}/${S3_KEY}" --region ${AWS_REGION}
echo -e "${GREEN}✅ S3 업로드 완료: s3://${S3_BUCKET}/${S3_KEY}${NC}"

# ============================================
# 3. 파라미터 파일 생성
# ============================================
echo -e "${YELLOW}📝 Step 3: 파라미터 파일 생성 중...${NC}"
PARAMS_FILE="${SCRIPT_DIR}/params-${ENV}.json"

# ✨ Lambda용 AWS 키 사용 (원래 .env에서 로드한 값)
cat > "$PARAMS_FILE" << EOF
{
    "S3Bucket": "${S3_BUCKET}",
    "S3Key": "${S3_KEY}",
    "SpringProfile": "lambda-${ENV}",
    "DbHost": "${DB_HOST}",
    "DbPort": "${DB_PORT}",
    "DbDatabase": "${DB_DATABASE}",
    "DbUsername": "${DB_USERNAME}",
    "DbPassword": "${DB_PASSWORD}",
    "AwsAccessKeyId": "${LAMBDA_AWS_ACCESS_KEY_ID}",
    "AwsSecretAccessKey": "${LAMBDA_AWS_SECRET_ACCESS_KEY}",
    "BucketName": "${BUCKET_NAME}",
    "PlaygroundApiUrl": "${PLAYGROUND_API_URL}",
    "PlaygroundApiToken": "${PLAYGROUND_API_URL_JWT_TOKEN}",
    "CrewApiUrl": "${CREW_API_URL}",
    "CrewApiToken": "${CREW_API_URL_JWT_TOKEN}",
    "AuthApiKey": "${AUTH_API_KEY}",
    "OurServiceName": "${OUR_SERVICE_NAME}",
    "AccessTokenSecret": "${ACCESS_TOKEN_SECRET}",
    "RefreshTokenSecret": "${REFRESH_TOKEN_SECRET}",
    "AdminTokenSecret": "${ADMIN_TOKEN_SECRET}",
    "OfficialApiKey": "${OFFICIAL_API_KEY}",
    "SentryDsn": "${DEV_SENTRY_DSN}",
    "RedisHost": "${REDIS_HOST}",
    "RedisPort": "${REDIS_PORT}"
}
EOF

echo -e "${GREEN}✅ 파라미터 파일 생성 완료${NC}"

# ============================================
# 4. SAM 배포
# ============================================
echo -e "${YELLOW}🔄 Step 4: SAM 배포 중...${NC}"
cd "$SCRIPT_DIR"

sam deploy \
    --template-file template-dev.yaml \
    --stack-name ${STACK_NAME} \
    --region ${AWS_REGION} \
    --capabilities CAPABILITY_IAM \
    --no-fail-on-empty-changeset \
    --parameter-overrides "$(cat "$PARAMS_FILE" | jq -r 'to_entries | map("\(.key)=\(.value)") | join(" ")')"

# 파라미터 파일 삭제 (보안)
rm -f "$PARAMS_FILE"

cd "$PROJECT_ROOT"

echo "============================================"
echo -e "${GREEN}✅ 배포 완료!${NC}"

# ============================================
# 5. API 엔드포인트 출력
# ============================================
API_ENDPOINT=$(aws cloudformation describe-stacks \
    --stack-name ${STACK_NAME} \
    --query "Stacks[0].Outputs[?OutputKey=='ApiEndpoint'].OutputValue" \
    --output text \
    --region ${AWS_REGION} 2>/dev/null || echo "")

if [ -n "$API_ENDPOINT" ] && [ "$API_ENDPOINT" != "None" ]; then
    echo -e "${GREEN}🌐 API Endpoint: ${API_ENDPOINT}${NC}"
    echo ""
    echo "테스트 명령어:"
    echo "  curl -X GET ${API_ENDPOINT}/v2/homepage"
    echo "  curl -X GET ${API_ENDPOINT}/v2/projects"
else
    echo -e "${YELLOW}⚠️ API Endpoint를 가져올 수 없습니다. AWS Console에서 확인하세요.${NC}"
fi
