import hudson.model.*

Build build = Executor.currentExecutor().currentExecutable
ParametersAction parametersAction = build.getAction(ParametersAction)
int VALUE_LENGTH = 60
int VARIABLE_LENGTH = 30
boolean isParameters = true

parametersAction.parameters.each { ParameterValue parameter ->
    String name = parameter.getName()
    String value = build.environment.get(name)
    if (isParameters) {
        println '\n\t+--------------------------------+--------------------------------------------------------------+'
        println String.format("\t| %-${VARIABLE_LENGTH + VALUE_LENGTH + 3}s |", "${build.environment.get('JOB_BASE_NAME')} will use these parameters")
        println '\t+--------------------------------+--------------------------------------------------------------+'
        isParameters = false
    }
    if (value.length() > VALUE_LENGTH) {
        int end = VALUE_LENGTH - 1
        int size = value.length() - 1
        boolean outWithName = true
        while (size > 0) {
            String outValue = ''
            if (!outWithName) {
                name = ''
            } else {
                outWithName = false
            }
            if (size < VALUE_LENGTH) {
                end = size
            }
            outValue = value[0..end]
            println String.format("\t| %-${VARIABLE_LENGTH}s | %-${VALUE_LENGTH}s |", "${name}", "${outValue}")
            size = size - VALUE_LENGTH
            if (end + 1 < value.size() - 1) {
                value = value[end + 1..value.size() - 1]
            }
        }
    } else {
        println String.format("\t| %-${VARIABLE_LENGTH}s | %-${VALUE_LENGTH}s |", "${name}", "${value}")
    }
    println '\t+--------------------------------+--------------------------------------------------------------+'
}

return
