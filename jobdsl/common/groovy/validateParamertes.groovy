import hudson.model.*
import hudson.AbortException

Build build = Executor.currentExecutor().currentExecutable
ParametersAction parametersAction = build.getAction(ParametersAction)

if (parametersAction != null) {
    parametersAction.parameters.each { ParameterValue parameter ->
        String name = parameter.getName()
        String value = parameter.getValue()
        println "[INFO]: ${name} is being validated..."
        if (value.contains(';') || value.contains('|') || value.contains(')') ||
                value.contains('*') || value.contains('@') || value.contains('?') ||
                value.contains('$') || value.contains('!') || value.contains('[') ||
                value.contains('>') || value.contains('<') || value.contains(']') ||
                value.contains('^') || value.contains('{') || value.contains('}') ||
                value.contains('\\') || value.contains('&') || value.contains('(')) {
            throw new AbortException("\033[0;31mPay attention on the ${name}=${value} parameter. It may contain a shell injection.\033[0m")
        }
    }
}
println '[INFO]: All parameters have been validated successfully.'
