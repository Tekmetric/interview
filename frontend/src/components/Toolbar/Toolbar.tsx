import { bindActionCreators, type Dispatch } from '@reduxjs/toolkit';
import { connect } from 'react-redux';
import { useIntl } from 'react-intl';
import { useCallback, useState } from 'react';
import debounce from 'lodash.debounce';
import { useTheme } from 'styled-components';

import {
	type ResourcesStateInterface,
	resourcesActions,
} from 'src/store/slices/resources';
import { type RootState } from 'src/store';

import Button from '../Button';
import {
	Container,
	SearchBox,
	SearchBoxContainer,
	SecondaryToolbar,
} from './Toolbar.styled';
import Refresh from '../icons/Refresh';

interface ToolbarInterface {
	searchText: ResourcesStateInterface['searchText'];
	setSearchText: (typeof resourcesActions)['setSearchText'];
	numberOfSelectedItems: ResourcesStateInterface['numberOfSelectedItems'];
	isDeleteLoading: ResourcesStateInterface['isDeleteLoading'];
	bulkDeleteResources: (typeof resourcesActions)['bulkDeleteResources'];
	refreshData: (typeof resourcesActions)['refreshData'];
}

function Toolbar({
	searchText,
	setSearchText,
	numberOfSelectedItems,
	isDeleteLoading,
	bulkDeleteResources,
	refreshData,
}: ToolbarInterface) {
	const { formatMessage } = useIntl();
	const [text, setText] = useState(searchText);
	const theme = useTheme();

	const debouncedSearch = useCallback(
		debounce((value: string) => {
			setSearchText(value);
		}, 500),
		[]
	);

	function handleChange(event: React.ChangeEvent<HTMLInputElement>) {
		const { value } = event.target;

		setText(value);
		debouncedSearch(value);
	}

	function handleDelete() {
		// eslint-disable-next-line no-alert
		if (confirm(formatMessage({ id: 'BULK_DELETE_CONFIRMATION' }))) {
			bulkDeleteResources();
		}
	}

	return (
		<Container>
			<SearchBoxContainer>
				{formatMessage({ id: 'FILTER_BY_TITLE' })}

				<SearchBox value={text} onChange={handleChange} />
			</SearchBoxContainer>

			<SecondaryToolbar>
				<Button
					disabled={numberOfSelectedItems === 0 || isDeleteLoading}
					onClick={handleDelete}
					buttonType="destructive"
				>
					{formatMessage({ id: 'DELETE' })}
				</Button>

				{numberOfSelectedItems > 0 &&
					formatMessage(
						{ id: 'SELECTED_RESOURCES_COUNT' },
						{ count: numberOfSelectedItems }
					)}

				<Button
					onClick={() => {
						refreshData();
					}}
					isRound
				>
					<Refresh fill={theme.textColor01} />
				</Button>
			</SecondaryToolbar>
		</Container>
	);
}

const mapStateToProps = (state: RootState) => ({
	searchText: state.resources.searchText,
	numberOfSelectedItems: state.resources.numberOfSelectedItems,
	isDeleteLoading: state.resources.isDeleteLoading,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
	setSearchText: bindActionCreators(resourcesActions.setSearchText, dispatch),
	refreshData: bindActionCreators(resourcesActions.refreshData, dispatch),
	bulkDeleteResources: bindActionCreators(
		resourcesActions.bulkDeleteResources,
		dispatch
	),
});

export default connect(mapStateToProps, mapDispatchToProps)(Toolbar);
