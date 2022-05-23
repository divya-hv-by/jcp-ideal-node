PACKAGE_NUMBER=$1
#USER_PWD=Dev-readonly-new:$password
USER_PWD=dev-readonly:$password
NEXUS_URL=https://nexus.yantriks.in/nexus/service/local/repositories
if [ "$#" -gt 1 ]
        REPO=$2
else
        REPO=release-candidate
fi


curl -v -u ${USER_PWD} --upload-file $WORKSPACE/JCPKakfacommits/cicd/jenkins/scripts/startup.sh ${NEXUS_URL}/${REPO}/content/com/yantriks/JCPKakfacommits/events/extensions/ideal-node-new/${PACKAGE_NUMBER}/startup.sh
curl -v -u ${USER_PWD} --upload-file $WORKSPACE/JCPKakfacommits/cicd/jenkins/scripts/Dockerfile ${NEXUS_URL}/${REPO}/content/com/yantriks/JCPKakfacommits/events/extensions/ideal-node-new/${PACKAGE_NUMBER}/Dockerfile

