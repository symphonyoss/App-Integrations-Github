[![Symphony Software Foundation - Incubating](https://cdn.rawgit.com/symphonyoss/contrib-toolbox/master/images/ssf-badge-incubating.svg)](https://symphonyoss.atlassian.net/wiki/display/FM/Incubating) [![Build Status](https://travis-ci.org/symphonyoss/App-Integrations-Github.svg?branch=dev)](https://travis-ci.org/symphonyoss/App-Integrations-Github) [![Dependencies](https://www.versioneye.com/user/projects/58d0495fcef50000242b6ebf/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/58d0495fcef50000242b6ebf)

_These informations cover only GitHub specific webhook configuration and the rendering pipeline. For more information about  Webhook Integration architecture, development environment, application bootstraping and building, please visit https://github.com/symphonyoss/App-Integrations-Zapier._

# GitHub WebHook Integration
The GitHub Webhook Integration will allow you to receive notifications in Symphony for many of the important actions taken by you and others in your repositories, such as a push of new commits, the opening of a pull request, or the creation of a release.

## How it works
As the GitHub admin of a repository, you can configure a WebHook to post messages to a URL you generate in the GitHub Application to start receiving notifications for the supported events. 

## Using a custom trust store
GitHub integration connects to the GitHub service to retrieve information about users (user name) and display it on webhook events. 

When using a custom trust store for the Integration Bridge, make sure it also includes the proper certificate to connect to the GitHub service. If GitHub is used as a cloud service, download the certificate from GitHub domain and add it to the custom trust store. In case GitHub is deployed on premises, make sure the trust store contains the certificate to connect to the on-prem server and the Integration Bridge can also access that server through the network.
  
In case the Integration Bridge can not access GitHub, webhooks will still work, but only usernames will be displayed on the events.

## What formats and events it supports and what it produces
As of the current version, one must set the WebHook Content type to application/json, as shown below:
![Selecting content type](src/docs/sample/sample_webhook_content_type.png)

Every integration will receive a [message](#github-json-message-sent-check-it) sent in a specific format (depending on the system it ingests) and will usually convert it into an object called **MessageML** before it reaches the Symphony platform.

The **MessageML** basically contains two structures:
- A HTML [template](#message-ml-template) used to format the message correctly. _It uses [Apache FreeMarker](http://freemarker.org) conventions to parse the message correctly._
- A [JSON](#message-ml-data-generated-by-the-parsers), contaning the relevant data sent by the webhook.

It will also, usually, identify the kind of message based on an "event" identifier, which varies based on the third-party system. _Note that currently only **application/json** media type is supported by GitHub integration._

You can find more details about entities and the Symphony Message ML format [here](https://github.com/symphonyoss/App-Integrations-Core#the-message-ml-format).

Currently we support the following ten events from GitHub:

> [Pull Request](https://developer.github.com/v3/activity/events/types/#pullrequestevent)
>
> [Push Changes](https://developer.github.com/v3/activity/events/types/#pushevent)
>
> [Review Comment on Pull Request](https://developer.github.com/v3/activity/events/types/#pullrequestreviewcommentevent)
>
> [Makes a Deployment](https://developer.github.com/v3/activity/events/types/#deploymentevent)
>
> [Deployment Status Changes](https://developer.github.com/v3/activity/events/types/#deploymentstatusevent)
>
> [Comment on Commits](https://developer.github.com/v3/activity/events/types/#commitcommentevent)
>
> [Comment on Issues](https://developer.github.com/v3/activity/events/types/#issuecommentevent)
>
> [Makes a Repository Public](https://developer.github.com/v3/activity/events/types/#publicevent)
>
> [Create a Release Tag](https://developer.github.com/v3/activity/events/types/#releaseevent)
>
> [Status](https://developer.github.com/v3/activity/events/types/#statusevent)

These can be found while configuring a WebHook on GitHub by the names mentioned here.

We identify these events by looking at a header parameter that GitHub sends with each request, denoted by the key *X-GitHub-Event*. 

Below we'll detail the **Pull Request** event, from its _application/json_ payload to the generated Symphony Message ML. _The other events follow the same format and behaviour._

### Pull Request
* ##### Event key: pull_request
* ##### Actions supported: *opened, assigned, closed, labeled and reopened*
* ##### JSON received from GitHub: ([check it](src/docs/sample/payload_xgithubevent_pull_request.json))
* ##### Message ML template
```html
<messageML>
  <div class="entity" data-entity-id="githubPullRequest">
    <card class="barStyle" iconSrc="${entity['githubPullRequest'].iconURL}" accent="gray">
      <header>
        <a href="${entity['githubPullRequest'].url}">Pull Request #${entity['githubPullRequest'].number} </a>
        <span class="tempo-text-color--normal">${entity['githubPullRequest'].title} - </span>
        <span class="tempo-text-color--green"><b>${entity['githubPullRequest'].action} </b></span>

        <#if entity['githubPullRequest'].action == 'assigned'>
          <span class="tempo-text-color--normal">to </span>
          <span class="tempo-text-color--normal"><b>${entity['githubPullRequest'].assignee.name} </b></span>
        <#elseif entity['githubPullRequest'].action == 'labeled'>
          <span class="tempo-text-color--normal">with </span>
          <span class="tempo-text-color--normal"><b>${entity['githubPullRequest'].label.name} </b></span>
        <#else>
          <span class="tempo-text-color--normal">by </span>
          <span class="tempo-text-color--normal"><b>${entity['githubPullRequest'].sender.name} </b></span>
        </#if>
      </header>
      <body>
        <p>
          <span class="tempo-text-color--secondary">Summary: </span>
          <span class="tempo-text-color--normal">${entity['githubPullRequest'].body}</span>
        </p>
        <p>
          <span class="tempo-text-color--secondary">Commits in this PR: </span>
          <span class="tempo-text-color--normal">${entity['githubPullRequest'].commits}</span>
        </p>
        <p>
          <span class="tempo-text-color--secondary">PR State: </span>
          <span class="tempo-text-color--normal">${entity['githubPullRequest'].state} </span>
          <span class="tempo-text-color--secondary">Merged: </span>
          <#if entity['githubPullRequest'].merged == 'true'>
            <span class="tempo-text-color--normal">Yes </span>
          <#else>
            <span class="tempo-text-color--normal">No </span>
          </#if>
        </p>
        <p>
          <span class="tempo-text-color--secondary">Repositories involved: </span>
          <span class="tempo-text-color--normal">Changes from </span>
          <a href="${entity['githubPullRequest'].repoHead.url}/tree/${entity['githubPullRequest'].repoHead.branch}">
            ${entity['githubPullRequest'].repoHead.fullName}:${entity['githubPullRequest'].repoHead.branch}
          </a>
          <span class="tempo-text-color--normal"> to </span>
          <a href="${entity['githubPullRequest'].repoBase.url}/tree/${entity['githubPullRequest'].repoBase.branch}">
            ${entity['githubPullRequest'].repoBase.fullName}:${entity['githubPullRequest'].repoBase.branch}
          </a>
        </p>
      </body>
    </card>
  </div>
</messageML>
```
* ##### Message ML data generated by the parsers
```json
{
  "githubPullRequest": {
    "type": "com.symphony.integration.github.event.v2.pullRequest",
    "version": "1.0",
    "iconURL": "symphony.com/img/github_logo.svg",
    "action": "opened",
    "number": "1",
    "state": "open",
    "title": "Update the README with new information",
    "body": "This is a pretty simple change that we need to pull into master.",
    "url": "https://github.com/baxterthehacker/public-repo/pull/1",
    "merged": "false",
    "commits": "1",
    "repoHead": {
      "type": "com.symphony.integration.github.repository",
      "version": "1.0",
      "fullName": "baxterthehacker/public-repo",
      "branch": "changes",
      "url": "https://github.com/baxterthehacker/public-repo"
    },
    "repoBase": {
      "type": "com.symphony.integration.github.repository",
      "version": "1.0",
      "fullName": "baxterthehacker/public-repo",
      "branch": "master",
      "url": "https://github.com/baxterthehacker/public-repo"
    },
    "sender": {
      "type": "com.symphony.integration.github.user",
      "version": "1.0",
      "name": "baxterthehacker"
    }
  }
}
```
* ##### Message rendered on Symphony

![Pull Request Opened rendered](src/docs/sample/sample_pull_request_opened_rendered.png)
