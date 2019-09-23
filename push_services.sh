
publish_for() {
  echo "About to publish ${1}"
  cd "${1}" || exit
  mvn $MAVEN_CLI_OPTS dockerfile:push
  cd .. && echo
}

publish_for ./AuthMs
publish_for ./AccountsMs
publish_for ./ConfigServer
publish_for ./EurekaServer
publish_for ./HystrixDashboard
publish_for ./SpringAdminServer
publish_for ./turbine-server
publish_for ./ui-gateway
publish_for ./ZuulApiProxy
