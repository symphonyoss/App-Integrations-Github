import React from 'react';
import ReactDOM from 'react-dom';
import './js/scripts.js';
export default class Setup extends React.Component {
	render() {
		return(
			<div>
				{/* Start editing area */}

				<h4>Step 1</h4>
				<p>Copy the URL so that you can use it later to configure the integration correctly.</p>
				<figure>
					<img src={require('./img/github-admin-webhooks.jpg')} alt="Webhook configuration"/>
				</figure>
				<h4>Step 2</h4>
				<p>In your Github account, select the repository you&rsquo;d like to receive notifications from. Choose Settings in the right navigation.</p>
				<figure>
					<img src={require('./img/github_settings_step2.png')} alt="Webhook configuration"/>
				</figure>
				<h4>Step 3</h4>
				<p>In the left navigation, select Webhooks, and then click the Add Webhook button.</p>
				<figure>
					<img src={require('./img/github_settings_step3.png')} alt="Webhook configuration"/>
				</figure>
				<h4>Step 4</h4>
				<p>Paste the URL you copied earlier under Payload URL.</p>
				<p>Choose the events you&rsquo;d like to be notified of. Currently, the following are supported: code pushes, pull requests, comments on pull requests, issues, comments on issues, merges, and the latest deployment status.</p>
				<p>Check the box next to Active to turn the webhook on.</p>
				<p>Click the Add webhook button.</p>
				<figure>
					<img src={require('./img/github_settings_step4.png')} alt="Webhook configuration"/>
				</figure>

				{/* End editing area */}
			</div>
		);
	}
}
