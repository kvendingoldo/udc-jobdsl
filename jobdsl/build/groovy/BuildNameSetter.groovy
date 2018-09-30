def build = Thread.currentThread().executable
def buildName = build.getEnvironment().get('BUILD_ID')
build.setDescription("build number is #${buildName}")
build.setDisplayName(build.buildVariableResolver.resolve('VERSION'))
