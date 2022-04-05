PACKAGE_NUMBER=$1
#USER_PWD=Dev-readonly-new:$password
USER_PWD=dev-readonly:$password
NEXUS_URL=https://nexus.yantriks.in/nexus/service/local/repositories
JFROG_URL=https://jdasoftware.jfrog.io/jdasoftware
JFROG_USER_PWD=ecom-ci:$jfrogpassword
if [ "$#" -gt 1 ]
then
        REPO=$2
		JFROG_REPO=libs-release-local
else
        REPO=release-candidate
		JFROG_REPO=libs-snapshot-local
fi


curl -v -u ${USER_PWD} --upload-file $WORKSPACE/JCPKakfacommits/cicd/jenkins/scripts/startup.sh ${NEXUS_URL}/${REPO}/content/com/yantriks/JCPKakfacommits/jcp-ideal/${PACKAGE_NUMBER}/startup.sh
curl -v -u ${USER_PWD} --upload-file $WORKSPACE/JCPKakfacommits/cicd/jenkins/scripts/Dockerfile ${NEXUS_URL}/${REPO}/content/com/yantriks/JCPKakfacommits/jcp-ideal/${PACKAGE_NUMBER}/Dockerfile


