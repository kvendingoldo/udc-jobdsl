package jobdsl.orchestrator.jobs

import com.kvendingoldo.jdcl.core.Functions

class UDC_Deploy_Orchestrator {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            logRotator(jobConfig.job.daysToKeepBuilds,
                    jobConfig.job.maxOfBuildsToKeep)
            wrappers {
                colorizeOutput()
                timestamps()
                preBuildCleanup()
            }
            parameters {
                stringParam {
                    name('VERSION')
                    defaultValue('')
                    description('Version of udc-petclinic project')
                    trim(true)
                }
            }
            steps {
                systemGroovy {
                    source {
                        stringSystemScriptSource {
                            script {
                                script(dslFactory.readFileFromWorkspace('jobdsl/common/groovy/printJobVariablesTable.groovy'))
                                sandbox(false)
                            }
                        }
                    }
                }
                systemGroovy {
                    source {
                        stringSystemScriptSource {
                            script {
                                script(dslFactory.readFileFromWorkspace('jobdsl/common/groovy/validateParamertes.groovy'))
                                sandbox(false)
                            }
                        }

                    }
                }
                downstreamParameterized {
                    trigger('../Deploy/UDC_Destroy_LLE') {
                        block {
                            buildStepFailure('UNSTABLE')
                            failure('UNSTABLE')
                            unstable('UNSTABLE')
                        }
                        parameters {
                            predefinedProp('VERSION', '${VERSION}')
                        }
                    }
                    trigger('../Deploy/UDC_Deploy_LLE') {
                        block {
                            buildStepFailure('UNSTABLE')
                            failure('UNSTABLE')
                            unstable('UNSTABLE')
                        }
                        parameters {
                            predefinedProp('VERSION', '${VERSION}')
                        }
                    }
                }
                buildNameUpdater {
                    fromFile(false)
                    buildName('${VERSION}')
                    fromMacro(false)
                    macroTemplate('')
                    macroFirst(false)
                }
            }
        }
    }
}
