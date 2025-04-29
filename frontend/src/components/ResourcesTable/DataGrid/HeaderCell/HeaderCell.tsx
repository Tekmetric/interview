import { useTheme } from 'styled-components';

import {
	HeaderActiveCell,
	IconWrapper,
	StyledHeaderCell,
	Text,
} from './HeaderCell.styled';
import { type DataGridInterface, type DataGridHeaderInterface } from '../types';
import SortableIcon from './assets/SortableIcon';
import AscendingIcon from './assets/AscendingIcon';

interface HeaderCellInterface
	extends Pick<DataGridInterface, 'onSort' | 'sortConfig'> {
	header: DataGridHeaderInterface;
}

function HeaderCell({ header, onSort, sortConfig }: HeaderCellInterface) {
	const theme = useTheme();

	if (!header.isSortable) {
		return (
			<StyledHeaderCell key={header.key}>
				<Text>{header.label}</Text>
			</StyledHeaderCell>
		);
	}

	if (header.isSortable) {
		return (
			<HeaderActiveCell
				key={header.key}
				onClick={() => {
					onSort(header.key);
				}}
			>
				<Text>{header.label}</Text>
				{sortConfig.column !== header.key && (
					<SortableIcon fill={theme.textColor02} />
				)}

				{sortConfig.column === header.key && (
					<IconWrapper shouldInvert={sortConfig.order === 'desc'}>
						<AscendingIcon fill={theme.textColor02} />
					</IconWrapper>
				)}
			</HeaderActiveCell>
		);
	}
}

export default HeaderCell;
