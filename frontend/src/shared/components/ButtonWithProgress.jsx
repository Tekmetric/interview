import { Button, CircularProgress } from '@mui/material';
import PropTypes from 'prop-types';

export default function ButtonWithProgress({
  loading = false,
  color = 'primary',
  children,
  ...props
}) {
  return (
    <Button color={color} disabled={loading} {...props}>
      {children}
      {loading && (
        <CircularProgress
          size={24}
          color={color}
          sx={{
            position: 'absolute',
            top: '50%',
            left: '50%',
            marginTop: '-12px',
            marginLeft: '-12px'
          }}
        />
      )}
    </Button>
  );
}

ButtonWithProgress.propTypes = {
  loading: PropTypes.bool,
  color: PropTypes.string,
  children: PropTypes.any
};
