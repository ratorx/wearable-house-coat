import React from 'react';
import { Navbar, Nav, NavItem, NavDropdown, MenuItem } from 'react-bootstrap';

const Navigation = (props) =>
	<Navbar fixedTop inverse collapseOnSelect className="navbar-left-no-margin navbar-styled">
		<Navbar.Header>
			<Navbar.Brand>
				<a onClick={props.onBrandClick}>Wearable House Coat</a>
			</Navbar.Brand>
			<Navbar.Toggle/>
		</Navbar.Header>
		<Navbar.Collapse>
			<Nav>
				{
					props.pages.map((page, i) => {
						if(typeof page.dropdown === "undefined") {
							return <NavItem key={i} active={page.name === props.activePage.name} onClick={() => props.onSelectPage(page)}>{page.name}</NavItem>
						} else {
							return <NavDropdown key={i} title={page.name} id={"main-navbar-dropdown-" + page.name}>
								{
									page.dropdown.map((subpage, j) =>
										<MenuItem key={i + "." + j} active={subpage.name === props.activePage.name} onClick={() => props.onSelectPage(subpage)}>{subpage.name}</MenuItem>
									)
								}
							</NavDropdown>
						}
					})
				}
			</Nav>
		</Navbar.Collapse>
	</Navbar>

export default Navigation