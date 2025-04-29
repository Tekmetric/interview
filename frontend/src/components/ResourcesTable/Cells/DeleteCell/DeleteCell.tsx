import { bindActionCreators, type Dispatch } from '@reduxjs/toolkit';
import { useIntl } from 'react-intl';
import { connect } from 'react-redux';

import Button from 'src/components/Button';
import { type RootState } from 'src/store';
import {
	type ResourceInterface,
	resourcesActions,
	type ResourcesStateInterface,
} from 'src/store/slices/resources';

interface DeleteCellInterface {
	item: ResourceInterface;
	deleteResource: (typeof resourcesActions)['deleteResource'];
	isDeleteLoading: ResourcesStateInterface['isDeleteLoading'];
}

function DeleteCell({
	item,
	deleteResource,
	isDeleteLoading,
}: DeleteCellInterface) {
	const { formatMessage } = useIntl();

	function handleOnClick(e: React.MouseEvent) {
		e.stopPropagation();

		// eslint-disable-next-line no-alert
		if (confirm(formatMessage({ id: 'DELETE_CONFIRMATION' }))) {
			deleteResource({ id: item.id });
		}
	}

	return (
		<Button
			buttonType="destructive"
			onClick={handleOnClick}
			disabled={isDeleteLoading}
		>
			{formatMessage({ id: 'DELETE' })}
		</Button>
	);
}

const mapStateToProps = (state: RootState) => ({
	isDeleteLoading: state.resources.isDeleteLoading,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
	deleteResource: bindActionCreators(resourcesActions.deleteResource, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(DeleteCell);
