import React from 'react';
import PropTypes from 'prop-types';
import { useTranslation } from 'react-i18next';
import { COLUMN_WIDTHS, classes } from '../lib/styles';
import { TableHeaderCell } from './TableCell';

const TableHeader = ({ isMobile }) => {
  const { t } = useTranslation();

  return (
    <div className={classes.tableHeaderContainer(isMobile)} role="rowgroup">
      <div className={classes.tableHeader(isMobile)} role="row">
        <TableHeaderCell width={COLUMN_WIDTHS.id}>{t('table.headers.number')}</TableHeaderCell>
        <TableHeaderCell width={COLUMN_WIDTHS.image} className={classes.tableHeaderCellImage(isMobile)}>{t('table.headers.image')}</TableHeaderCell>
        <TableHeaderCell width={COLUMN_WIDTHS.name} className={classes.tableHeaderCell(isMobile)}>{t('table.headers.name')}</TableHeaderCell>
        <TableHeaderCell width={COLUMN_WIDTHS.height} className={classes.tableHeaderCell(isMobile)}>{t('table.headers.height')}</TableHeaderCell>
        <TableHeaderCell width={COLUMN_WIDTHS.weight} className={classes.tableHeaderCell(isMobile)}>{t('table.headers.weight')}</TableHeaderCell>
        {!isMobile && <TableHeaderCell width={COLUMN_WIDTHS.stats} className={classes.tableHeaderCell(isMobile)}>{t('table.headers.stats')}</TableHeaderCell>}
      </div>
    </div>
  );
};

TableHeader.propTypes = {
  isMobile: PropTypes.bool.isRequired,
};

export default TableHeader;
