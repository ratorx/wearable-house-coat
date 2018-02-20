import React from 'react';
import './App.css';
import Lorem from './Lorem.js';
import { Navbar, Nav, NavItem, NavDropdown, MenuItem } from 'react-bootstrap';

class App extends React.Component {

	// Pages enum
	Pages = [
		{
			name: "Overview",
			active: false,
			action: () => {
				console.log("Overview");
			}
		},
		{
			name: "Setup",
			dropdown: [
				{
					name: "Rooms",
					active: false,
					action: () => {
						console.log("Rooms");
					}
				},
				{
					name: "Devices",
					active: true,
					action: () => {
						console.log("Devices");
					}
				},
				{
					name: "Groups",
					active: false,
					action: () => {
						console.log("Groups");
					}
				}
			]
		},
		{
			name: "Help",
			active: false,
			action: () => {
				console.log("HEEEEEEELP!!!");
			}
		}
	]

	render() {
		return <div>
			{/* <NavBar title="Wearable House Coat" pages={this.Pages}/>*/}
			<Navbar fixedTop inverse>
				<Navbar.Header>
					<Navbar.Brand>
						<a href="#home">Wearable House Coat</a>
					</Navbar.Brand>
					<Navbar.Toggle/>
				</Navbar.Header>
				<Navbar.Collapse>
					<Nav>
						{
							this.Pages.map((page, i) => {
								if(typeof page.dropdown === "undefined") {
									return <NavItem key={i} active={page.active} onClick={page.action}>{page.name}</NavItem>
								} else {
									return <NavDropdown key={i} title={page.name} id={"main-navbar-dropdown-" + page.name}>
										{
											page.dropdown.map((subpage, j) =>
												<MenuItem key={i + "." + j} active={subpage.active} onClick={subpage.action}>{subpage.name}</MenuItem>
											)
										}
									</NavDropdown>
								}
							})
						}
					</Nav>
				</Navbar.Collapse>
			</Navbar>
			<Lorem/>
		</div>
	}
}

export default App;
