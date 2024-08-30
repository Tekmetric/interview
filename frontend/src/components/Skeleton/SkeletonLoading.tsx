import Grid from '@mui/material/Unstable_Grid2';
import Box from '@mui/material/Box';
import Skeleton from '@mui/material/Skeleton';
import Card from '@mui/material/Card';

const SkeletonLoading = (): JSX.Element => (
  <Grid
    container
    spacing={4}
    columns={12}
    padding={3}
    data-testid="skeleton-loading"
    sx={{
      flex: '1 1 auto',
      overflow: 'auto',
      marginTop: 0,
      paddingTop: 0,
    }}
  >
    {Array.from(new Array(10)).map((_i, idx) => (
      <Grid xs={12} sm={6} md={3} key={idx}>
        <Card sx={{ padding: '16px 16px 24px 16px', gap: '16px' }}>
          <Skeleton sx={{ height: '32px' }} />
          <Skeleton variant="rectangular" height={150} />
          <Box sx={{ pt: 0.5 }}>
            <Skeleton width="40%" />
            <Skeleton width="40%" />
          </Box>
        </Card>
      </Grid>
    ))}
  </Grid>
);

export default SkeletonLoading;
