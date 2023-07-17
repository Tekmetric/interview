import {
  DragDropContext,
  DraggableProvided,
  DraggableStateSnapshot,
  Draggable,
} from '@hello-pangea/dnd';
import { Column, Card } from './index';

type ContainerProps = {
  list: { beforePet: Array<any>; afterPet: Array<any> };
  onDragEnd: (result: any) => void;
};

const Container = ({ list, onDragEnd }: ContainerProps) => {
  return (
    <DragDropContext onDragEnd={onDragEnd}>
      <div className="flex min-w-[70%] justify-center p-3">
        <Column title="To be petted" droppableId="beforePet">
          {list.beforePet.map((item, index) => (
            <Draggable key={item.id} draggableId={item.id + ''} index={index}>
              {(
                provided: DraggableProvided | any,
                snapshot: DraggableStateSnapshot
              ) => (
                <div>
                  <div
                    ref={provided.innerRef}
                    {...provided.draggableProps}
                    {...provided.dragHandleProps}
                  >
                    <Card dogEntry={item} isDragging={snapshot.isDragging} />
                  </div>
                </div>
              )}
            </Draggable>
          ))}
        </Column>
        <Column title="Petted" droppableId="afterPet">
          {list.afterPet.map((item, index) => (
            <Draggable
              draggableId={item.id + ' after'}
              index={index}
              key={item.id}
            >
              {(provided, snapshot) => (
                <div
                  ref={provided.innerRef}
                  {...provided.draggableProps}
                  {...provided.dragHandleProps}
                >
                  <Card dogEntry={item} isDragging={snapshot.isDragging} />{' '}
                </div>
              )}
            </Draggable>
          ))}
        </Column>
      </div>
    </DragDropContext>
  );
};
export default Container;
