import React from 'react';
import { FormControl } from 'react-bootstrap';
import { HuePicker } from 'react-color';
import './Spotify.css';

class Spotify extends React.Component{
	static get methods() {
		return [
			{
				method: "setPlaying",
				display_name: "Playing",
				parameters: ["java.lang.Boolean"],
				default_arguments: ["false"]
			}, {
				method: "setVolume",
				display_name: "Set Volume",
				parameters: ["java.lang.Integer"],
				default_arguments: [0]
			}
		]
	}

	onChangeMethod(e) {
		let newMethod = e.target.value;
		let method = Spotify.methods.find(m => m.method === newMethod)
		this.props.onChange({
			device: this.props.action.device,
			method: newMethod,
			parameters: method.parameters.slice(),
			arguments: method.default_arguments.slice()
		});
	}

	onChangePlaying(val) {
		this.props.onChange({
			device: this.props.action.device,
			method: "setPlaying",
			parameters: ["java.lang.Boolean"],
			arguments: [val]
		})
	}

	onChangeVolume(clr) {
		this.props.onChange({
			device: this.props.action.device,
			method: "setVolume",
			parameters: ["java.lang.Integer"],
			arguments: [Math.round(clr.hsl.h * 99 / 360)]
		})
	}

	render() {
		return <div>
			<FormControl componentClass="select" value={this.props.action.method ? this.props.action.method : "empty"} onChange={this.onChangeMethod.bind(this)}>
				<option value="empty" disabled>Select Action</option>
				{
					Spotify.methods.map((method, i) =>
						<option value={method.method} key={i}>{method.display_name}</option>
					)
				}
			</FormControl>
			{
				(this.props.action.method === "setPlaying") ?
					<FormControl componentClass="select" value={this.props.action.arguments[0]} onChange={this.onChangePlaying.bind(this)}>
						<option value="true">Yes</option>
						<option value="false">No</option>
					</FormControl>		
				: (this.props.action.method === "setVolume") ?
					<div>
						{/*unintuitive hack to preserve consistency of sliders: restyled Hue picker*/}
						{console.log(this.props.action.arguments[0])}
						<HuePicker
							className="volume-picker"
							color={{h: this.props.action.arguments[0] * 360 / 100, s: 1, l: 0.5}}
							onChange={this.onChangeVolume.bind(this)}
						/>
					</div>
				: null
			}
		</div>
	}
}
	

export default Spotify