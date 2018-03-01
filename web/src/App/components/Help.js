import React from 'react';
import { PageHeader } from 'react-bootstrap';
import Lorem from './Lorem.js';

const Help = (props) => {
	return <div>
		<PageHeader>Help</PageHeader>
		<h3>Hello, {props.name}</h3>
		<p>{"If you're reading this, it means something probably went wrong"}</p>
		<p>{"You'll find the best help on our <a href=\"https://github.com/ratorx/wearable-house-coat\">GitHub page</a>, but we'll try to answer common issues here.}"}</p>
		<br/>
		<h4>{"\"My lights keep going on and off\""}</h4>
		<p>This is most likely an issue {"with"} the location system. You should {"try"} recalibrating your location, to make the tracking more accurate.</p>
		<br/>
		<h4>{"\"The Hue controller has gone green."}?</h4>
		<p>The green light means the controller is connected, but {"hasn't"} configured properly. Just hit the main button on the bridge and it should {"sort"} itself out.</p>
		<br/>
		<h4>{"\"How do I change the colour of my lights?\""}</h4>
		<p>Press and hold the button of the light (or group of lights) you want to change. This will bring up the control panel; you can then press the big preview circle to display the colour selector</p>
		<br/>
		<h4>{"\"I tried pressing the skip button on my music, but it's greyed out and doesn't do anything.\""}</h4>
		<p>This means that the skip has been disabled, most likely because it isn{"\'"}t supported by your device.</p>
		<br/>
		<h4>{"\"I\'m using a router to extend my work/uni network, but nothing is working.\""}</h4>
		<p>Some networks <em>really</em> {"don\'t"} like to be extended. You will need to find an alternative connection.</p>
		<br/>
		<h4>{"\"I have an excellent idea for the project.\""}</h4>
		<p>Let us know by emailing us at <a href="mailto:clquebec-admins@srcf.net?subject=Wearable%20House%20%Coat idea!">clquebec-admins@srcf.net</a></p>
		<br/>
		<h4>{"\"I hit the ship, but they used a decoy\""}</h4>
		<p>Try using some weird poisonous space centipedes, that should work.</p>
		<br/>
	</div>
}

export default Help
