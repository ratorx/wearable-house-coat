import React from 'react';
import { ListGroup, ListGroupItem } from 'react-bootstrap';
import { SortableContainer, SortableElement, SortableHandle } from 'react-sortable-hoc';
import dragIcon from '../res/drag.png';
import './Sortable.css'

const DragHandle = SortableHandle(() => <img className="handle" src={dragIcon} alt="drag handle"/>);

const SortableDevice = SortableElement(({device}) =>
	<ListGroupItem>
		<DragHandle/>
		{device.config.name}
	</ListGroupItem>
)

const SortableDeviceList = SortableContainer(({devices}) =>
	<ListGroup>
		{
			devices.map((device, i) =>
				<SortableDevice key={i} index={i} device={device}/>
			)
		}
	</ListGroup>
)

export default SortableDeviceList