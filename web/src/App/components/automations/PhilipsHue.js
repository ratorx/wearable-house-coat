import React from 'react';
import { FormControl } from 'react-bootstrap';
import { HuePicker } from 'react-color';
import './PhilipsHue.css'

class PhilipsHue extends React.Component {
	static get methods() {
		return [
			{
				method: "setLightColor",
				display_name: "Set Colour",
				parameters: ["java.lang.Integer"],
				default_arguments: [(0xFF << 16).toString()]
			}, {
				method: "setBrightness",
				display_name: "Set Brightness",
				parameters: ["java.lang.Integer"],
				default_arguments: ["0"]
			}, {
				method: "enable",
				display_name: "Turn On",
				parameters: [],
				default_arguments: []
			}, {
				method: "disable",
				display_name: "Turn Off",
				parameters: [],
				default_arguments: []
			},
		]
	}

	constructor(props) {
		super(props);
		this.state = {
			color: (props.action.method === "setLightColor") ? this.colorFromInt(parseInt(props.action.arguments[0], 10)) : null
		}
	}

	onChangeMethod(e) {
		let newMethod = e.target.value;
		let method = PhilipsHue.methods.find(m => m.method === newMethod)
		this.props.onChange({
			device: this.props.action.device,
			method: newMethod,
			parameters: method.parameters.slice(),
			arguments: method.default_arguments.slice()
		});
		if(newMethod === "setLightColor")
			this.setState(() => {
				return {
					color: this.colorFromInt(parseInt(method.default_arguments[0], 10))
				}
			})
	}

	colorFromInt(clr) {
		return {
			r: (clr >> 16) & 0xFF,
			g: (clr >> 8) & 0xFF,
			b: clr & 0xFF
		};
	}

	colorToHexString(clr) {
		return "#" + clr.r.toString(16).padStart(2, "0") + clr.g.toString(16).padStart(2, "0") + clr.b.toString(16).padStart(2, "0");
	}

	onChangeColour(clr) {
		this.setState(() => {
			return {
				color: clr.rgb
			}
		})
	}

	onChangeColourComplete(clr) {
		this.props.onChange({
			device: this.props.action.device,
			method: "setLightColor",
			parameters: ["java.lang.Integer"],
			arguments: [(((clr.rgb.r & 0xFF) << 16) | ((clr.rgb.g & 0xFF) << 8) | (clr.rgb.b & 0xFF)).toString()]
		})
	}

	onChangeBrightnessComplete(clr) {
		this.props.onChange({
			device: this.props.action.device,
			method: "setBrightness",
			parameters: ["java.lang.Integer"],
			arguments: [(Math.round(clr.hsl.h * 255 / 360)).toString()]
		})
	}

 	render() {
		return <div>
			<FormControl componentClass="select" value={this.props.action.method ? this.props.action.method : "empty"} onChange={this.onChangeMethod.bind(this)}>
				<option value="empty" disabled>Select Action</option>
				{
					PhilipsHue.methods.map((method, i) =>
						<option value={method.method} key={i}>{method.display_name}</option>
					)
				}
			</FormControl>
			{
				(this.props.action.method === "setLightColor") ?
					<div>
						<div className="color-display" style={{backgroundColor: this.colorToHexString(this.state.color)}}/>
						<HuePicker
							className="color-picker"
							color={this.state.color}
							onChangeComplete={this.onChangeColourComplete.bind(this)}
							onChange={this.onChangeColour.bind(this)}
						/>
					</div>
				: (this.props.action.method === "setBrightness") ?
					<div>
						{/*unintuitive hack to preserve consistency of sliders: restyled Hue picker*/}
						<HuePicker
							className="bright-picker"
							color={{h: this.props.action.arguments[0] * 360 / 256, s: 1, l: 0.5}}
							onChangeComplete={this.onChangeBrightnessComplete.bind(this)}
						/>
					</div>
				: (this.props.action.method === "enable") ?
					null
				: (this.props.action.method === "disable") ?
					null
				: null
			}
		</div>
	}
}

export default PhilipsHue