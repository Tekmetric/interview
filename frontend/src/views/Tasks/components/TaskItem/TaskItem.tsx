import { FunctionComponent } from 'react';
import { Card, CardContent, Grid } from '@mui/material';
import { Task } from '@/store/data/task/types.ts';
import TaskFields from '@/views/Tasks/components/TaskItem/components/TaskFields.tsx';

interface TaskItemProps {
  task: Task;
}

const TaskItem: FunctionComponent<TaskItemProps> = ({ task }) => (
  <Grid item xs={12} sm={6} md={3} key={task.id} data-testid={`task-id-${task.id}`}>
    <Card
      tabIndex={0}
      sx={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
      }}
    >
      <CardContent
        sx={{
          display: 'flex',
          flexDirection: 'column',
          gap: '16px',
        }}
      >
        <TaskFields {...task} />
      </CardContent>
    </Card>
  </Grid>
);

export default TaskItem;
