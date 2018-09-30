package jobdsl.build.jobs

import com.kvendingoldo.jdcl.core.Functions

class UdcPreCommit {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            description(jobConfig.job.description)
            label(jobConfig.job.label)
            concurrentBuild()
            logRotator(jobConfig.job.daysToKeepBuilds, jobConfig.job.maxOfBuildsToKeep)
            environmentVariables {
                env('GENERATED_VERSION_TYPE', jobConfig.job.generatedVersionType)
                overrideBuildParameters(true)
            }
            wrappers {
              colorizeOutput()
              timestamps()
              preBuildCleanup()
            }
            scm {
                git {
                    remote {
                        credentials(jobConfig.job.credentials.github)
                        github(jobConfig.job.repository, 'ssh')
                        refspec('+refs/pull/*:refs/remotes/origin/pr/*')
                    }
                    branch('${sha1}')
                    extensions {
                        wipeOutWorkspace()
                        submoduleOptions {
                            recursive()
                        }
                    }
                }
            }
            triggers {
              ghprbTrigger {
                  blackListCommitAuthor('')
                  blackListLabels('')
                  whiteListLabels('')
                  includedRegions('')
                  excludedRegions('')
                  adminlist('kvendingoldo')
                  whitelist('kvendingoldo',)
                  orgslist('')
                  cron('*/1 * * * *')
                  // When filled, commenting this phrase in the pull request will trigger a build.
                  triggerPhrase('')
                  // When checked, only commenting the trigger phrase in the pull request will trigger a build.
                  onlyTriggerPhrase(false)
                  // Checking this option will disable regular polling (cron) for changes in GitHub and will try to create a GitHub hook.
                  useGitHubHooks(false)
                  // This is dangerous!!!
                  permitAll(false)
                  autoCloseFailedPullRequests(false)
                  displayBuildErrorsOnDownstreamBuilds(false)
                  commentFilePath('')
                  // When filled, adding this phrase to the pull request title or body will not trigger a build.
                  skipBuildPhrase('') // .*\[skip\W+ci\].*
                  // Adding branches to this whitelist allows you to selectively test pull requests destined for these branches only.
                  whiteListTargetBranches {}
                  // Adding branches to this blacklist allows you to prevent pull requests for specific branches.
                  blackListTargetBranches {}
                  // Use this option to allow members of whitelisted organisations to behave like admins, i.e. whitelist users and trigger pull request testing.
                  allowMembersOfWhitelistedOrgsAsAdmin(false)
                  msgSuccess('SUCCESS')
                  msgFailure('FAILTURE')
                  commitStatusContext('')
                  gitHubAuthId('lol')
                  // Set the build description.
                  buildDescTemplate('')
                  extensions {}
                }
                githubPullRequest {
                    cron('*/1 * * * *')
                    permitAll()
                }
            }
            steps {
              gitHubSetCommitStatusBuilder {
                statusMessage {
                    content('pending')
                }
              }
              shell('gcloud docker -a')
              shell(dslFactory.readFileFromWorkspace(jobConfig.job.shellScript))
              envInjectBuilder {
                  propertiesFilePath('version.properties')
                  propertiesContent('')
              }
              dsl {
                  text(dslFactory.readFileFromWorkspace('jobdsl/build/groovy/BuildNameSetter.groovy'))
              }
              maven {
                goals('clean install')
                goals('-B')
                goals('-C')
                goals('-q')
                goals(' -Pimage')
                goals('-Ddocker.registry.host=gcr.io')
              }
            }
            publishers {
              gitHubCommitNotifier {
                  resultOnFailure('fail')
                  statusMessage {
                    content('ok')
                  }
                }
              }
        }
    }
}
