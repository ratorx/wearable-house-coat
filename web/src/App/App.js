import React from 'react';
import './App.css';
import Overview from './components/Overview.js'
import SetRooms from './components/SetRooms.js'
import SetDevices from './components/SetDevices.js'
import SetGroups from './components/SetGroups.js'
import Help from './components/Help.js'
import { Navbar, Nav, NavItem, NavDropdown, MenuItem } from 'react-bootstrap';
import DeviceInfo from "./util/DeviceInfo.js"

class App extends React.Component {
	pages = [
		{
			name: "Overview",
			component: null,
			onPageLoad: (page) => {
				page.component = <Overview/>
			}
		},
		{
			name: "Setup",
			dropdown: [
				{
					name: "Rooms",
					component: null,
					onPageLoad: (page) => {
						page.component = <SetRooms/>
					}
				},
				{
					name: "Devices",
					component: null,
					onPageLoad: (page) => {
						page.component = <SetDevices devices={this.state.deviceInfo.info.devices.filter(dev => dev.type !== "DeviceGroup")} rooms={this.state.deviceInfo.info.rooms}/>
					}
				},
				{
					name: "Groups",
					component: null,
					onPageLoad: (page) => {
						page.component = <SetGroups/>
					}
				}
			]
		},
		{
			name: "Help",
			component: null,
			onPageLoad: (page) => {
				page.component = <Help/>
			}
		}
	]

	constructor(){
		super();
		this.state = {
			currentPage: this.pages[1].dropdown[1],
			deviceInfo: new DeviceInfo()
		};
		this.state.currentPage.onPageLoad(this.state.currentPage);
	}

	setCurrentPage(page) {
		this.setState(() => {return {currentPage: page}});
		page.onPageLoad(page);
	}

	render() {
		return <div>
			<Navbar fixedTop inverse collapseOnSelect className="navbar-left-no-margin navbar-styled">
				<Navbar.Header>
					<Navbar.Brand>
						<a onClick={() => {this.setCurrentPage(this.pages[0])}}>Wearable House Coat</a>
					</Navbar.Brand>
					<Navbar.Toggle/>
				</Navbar.Header>
				<Navbar.Collapse>
					<Nav>
						{
							this.pages.map((page, i) => {
								if(typeof page.dropdown === "undefined") {
									return <NavItem key={i} active={page.name === this.state.currentPage.name} onClick={() => {this.setCurrentPage(page)}}>{page.name}</NavItem>
								} else {
									return <NavDropdown key={i} title={page.name} id={"main-navbar-dropdown-" + page.name}>
										{
											page.dropdown.map((subpage, j) =>
												<MenuItem key={i + "." + j} active={subpage.name === this.state.currentPage.name} onClick={() => {this.setCurrentPage(subpage)}}>{subpage.name}</MenuItem>
											)
										}
									</NavDropdown>
								}
							})
						}
					</Nav>
				</Navbar.Collapse>
			</Navbar>
			<div className="content">
				{this.state.currentPage.component}
			</div>
		</div>
	}
}

export default App;
