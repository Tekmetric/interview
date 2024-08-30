import { FunctionComponent, memo } from 'react';
import TaskItem from '../TaskItem';
import { Task } from '@/store/data/task/types.ts';
import { Box, Grid, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import gridListStyles from '@/views/Tasks/styles/gridListStyles.ts';

interface TaskListProps {
  tasks: Task[];
}

const TaskList: FunctionComponent<TaskListProps> = ({ tasks }) => {
  const { t: translate } = useTranslation();

  if (!tasks.length) {
    return (
      <Box padding="2rem">
        <Typography>{translate('app.translation.tasks.NoResultsFound')}</Typography>
      </Box>
    );
  }
  return (
    <Grid container spacing={4} columns={12} padding={3} sx={gridListStyles} data-testid="tasks-list">
      {tasks.map(item => (
        <TaskItem task={item} key={item.id} />
      ))}
    </Grid>
  );
};

export default memo(TaskList);
