import { FunctionComponent } from 'react';
import { Typography, Button, Box } from '@mui/material';
import { Link } from 'react-router-dom';
import RouterUrl from '@/types/RouterUrl.ts';
import { fadeInDown, pulse } from './styles/animations';
import EmojiEmotionsIcon from '@mui/icons-material/EmojiEmotions';
import { useTranslation } from 'react-i18next';

const Home: FunctionComponent = () => {
  const { t: translate } = useTranslation();

  return (
    <Box
      display="flex"
      flexDirection="column"
      justifyContent="center"
      alignItems="center"
      height="100vh"
      textAlign="center"
      gap="32px"
      padding="32px"
    >
      <Typography
        variant="h4"
        whiteSpace="nowrap"
        sx={{
          animation: `${fadeInDown} 1.5s ease-in-out`,
          display: 'flex',
          gap: '8px',
          alignItems: 'center',
        }}
      >
        {translate('app.translation.HelloMessage')}
        <EmojiEmotionsIcon sx={{ fontSize: '3rem' }} />
      </Typography>
      <Button
        component={Link}
        to={RouterUrl.Tasks}
        variant="contained"
        color="primary"
        size="large"
        data-testid="view-tasks-link-button"
        aria-label={translate('app.translation.button.ViewTasks')}
        sx={{
          animation: `${pulse} 1.5s infinite`,
          padding: '12px 32px',
        }}
      >
        {translate('app.translation.button.ViewTasks')}
      </Button>
    </Box>
  );
};

export default Home;
