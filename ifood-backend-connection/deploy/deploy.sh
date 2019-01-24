#!/bin/sh

set -e
#### Local task
#  VERSION_PROJECT
#  COOKBOOK_REPO_PATH
#  COOKBOOK_NAME
#  TARGET_HOST
#  TARGET_KEY_NAME
#  ENVIRONMENT
####
VERSION_TO_BUILD="$SOURCE_BUILD_NUMBER"
if [ -z "$VERSION_TO_BUILD" ]; then
   VERSION_TO_BUILD=`curl <archiva.apache repository> | grep -oPm1 "(?<=<release>)[^<]+" | cut -d'-' -f 2`
   echo "No version provided. Deploying last good build: $VERSION_TO_BUILD."
fi
echo "SOURCE_BUILD_NUMBER=$VERSION_TO_BUILD" > version-to-build.properties

if [ -z "$SSH_KEYS_PATH" ]; then
   SSH_KEYS_PATH=/var/lib/jenkins/keys
   echo "No SSH_KEYS_PATH provided, using /var/lib/jenkins/keys"
fi

if [ -z "$SSH_HOSTS_FILE" ]; then
   SSH_HOSTS_FILE=/var/lib/jenkins/.ssh/known_hosts
   echo "No SSH_HOSTS_FILE provided, using ~/.ssh/known_hosts"
fi


if [ -z "$MAJOR_VERSION" ]; then
   MAJOR_VERSION=1.1
   echo "No MAJOR_VERSION provided, using 1.1"
fi

cat << TEMPLATE_EOF > script.template.txt

# Clean up

rm -rf berks-cookbooks
rm -rf cookbooks
rm -rf data_bags
rm -rf $COOKBOOK_NAME
mkdir -p data_bags/vault

git clone $COOKBOOK_REPO_PATH $COOKBOOK_NAME
berks vendor --berksfile=$COOKBOOK_NAME/Berksfile
ln -s berks-cookbooks cookbooks

echo "{\"build_version\": \"$VERSION_TO_BUILD\", \"major_version\": \"$MAJOR_VERSION\", \"environment\":\"$ENVIRONMENT\"}" > config.json

sudo chef-client -z -o $COOKBOOK_NAME -j /home/ubuntu/config.json
TEMPLATE_EOF

cat script.template.txt | ssh -i $SSH_KEYS_PATH/$TARGET_KEY_NAME -o UserKnownHostsFile=$SSH_HOSTS_FILE ubuntu@$TARGET_HOST
rm script.template.txt

if [ $? != 0 ]; then
  echo If this fails, it may well be that known_hosts needs to be updated. You can do so by sshing into the target as jenkins.
  exit 1
fi