import { bindActionCreators, type Dispatch } from '@reduxjs/toolkit';
import { connect } from 'react-redux';

import {
	type ResourcesStateInterface,
	resourcesActions,
} from 'src/store/slices/resources';
import { type RootState } from 'src/store';

import { Container } from './Toolbar.styled';

interface ToolbarInterface {
	searchText: ResourcesStateInterface['searchText'];
	setSearchText: (typeof resourcesActions)['setSearchText'];
}

function Toolbar({ searchText, setSearchText }: ToolbarInterface) {
	return <Container>Toolbar</Container>;
}

const mapStateToProps = (state: RootState) => ({
	searchText: state.resources.searchText,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
	setSearchText: bindActionCreators(resourcesActions.setSearchText, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(Toolbar);
