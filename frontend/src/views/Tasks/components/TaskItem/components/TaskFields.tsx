import { FunctionComponent, useMemo } from 'react';
import { Typography, Box, Chip } from '@mui/material';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import { useTranslation } from 'react-i18next';
import { getTranslatedStatus } from '@/views/Tasks/utils/mapStatusToTranslations.ts';
import { statusColors } from '../../../utils/mapStatusToColors';

interface TaskItemContentProps {
  title: string;
  description: string;
  status: string;
  dueDate: string;
  creationDate: string;
}

const TaskFields: FunctionComponent<TaskItemContentProps> = ({ title, description, status, dueDate, creationDate }) => {
  const { t: translate } = useTranslation();

  const translatedStatus = useMemo(() => getTranslatedStatus(translate), [translate]);

  return (
    <>
      <Box display="flex" alignItems="center" justifyContent="space-between" gap="8px">
        <Typography
          variant="h6"
          component="div"
          title={title}
          sx={{
            whiteSpace: 'nowrap',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
          }}
        >
          {title}
        </Typography>
        <Chip
          label={translatedStatus[status]}
          color={statusColors[status]}
          variant="outlined"
          sx={{ fontWeight: 'bold' }}
        />
      </Box>
      <Box
        sx={{
          height: '100px',
        }}
      >
        <Typography
          variant="body2"
          color="text.secondary"
          paragraph
          title={description}
          sx={{
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            WebkitBoxOrient: 'vertical',
            WebkitLineClamp: 3,
            height: '100%',
          }}
        >
          {description}
        </Typography>
      </Box>
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          gap: '8px',
        }}
      >
        <Box display="flex" alignItems="center" gap="4px">
          <CalendarTodayIcon fontSize="small" />
          <Typography variant="body2" color="text.secondary">
            {translate('app.translation.task.DueDate')}: {dueDate}
          </Typography>
        </Box>
        <Box display="flex" alignItems="center" gap="4px">
          <AccessTimeIcon fontSize="small" />
          <Typography
            variant="body2"
            color="text.secondary"
            sx={{
              fontStyle: 'italic',
              display: 'flex',
              alignItems: 'center',
            }}
          >
            {translate('app.translation.task.Created')}: {creationDate}
          </Typography>
        </Box>
      </Box>
    </>
  );
};

export default TaskFields;
