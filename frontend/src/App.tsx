import React, { useCallback, useEffect } from 'react';
import { TableContainer, Table, TableCaption, Thead, Tr, Th, Tbody, Td, Skeleton, Stack, Button } from '@chakra-ui/react';
import DeleteConfirmationModal from './components/DeleteConfirmationModal';
import GrassBottom from './components/GrassBottom';
import TableActionMenu from './components/TableActionMenu';
import { TeeTime } from './interfaces/tee-time.interface';
import AddEditTeeTimeModal from './components/AddEditTeeTimeModal';

export default function App() {

    const [rows, setRows] = React.useState([]);
    const [confirmationModalOpen, setConfirmationModalOpen] = React.useState(false);
    const [ upsertModalOpen, setUpsertModalOpen ] = React.useState(false);
    const [selectedTeeTime, setSelectedTeeTime] = React.useState<TeeTime | null>(null);

    const fetchTeeTimes = useCallback(async () => {
      const response = await fetch('http://localhost:8080/tee-times');
      const data = await response.json();
      console.log(data)
      setRows(data);
    }, []);

    useEffect(() => {
      fetchTeeTimes();
    }, [fetchTeeTimes]);

    const onEditClicked = (teeTime: TeeTime) => {
      setSelectedTeeTime(teeTime);
      setUpsertModalOpen(true);
    }

    const onDeleteClicked= (teeTime: TeeTime) => {
      setSelectedTeeTime(teeTime);
      setConfirmationModalOpen(true);
    }

    const closeConfirmationModal = async () => {
      setConfirmationModalOpen(false);
      setSelectedTeeTime(null);
      await fetchTeeTimes();
    }

    const onAddEditClick = () => {
      setUpsertModalOpen(true);
    }

    const handleAddEditClose = async () => {
      setUpsertModalOpen(false);
      setSelectedTeeTime(null);
      await fetchTeeTimes();
    }

    return (
    <div className="flex flex-col justify-center h-screen w-full p-2">
        <div className="self-center text-3xl" >Tee Time Caddy</div>
        <GrassBottom numberOfGrassBlades={100} />
        {upsertModalOpen && 
          <AddEditTeeTimeModal 
            open={upsertModalOpen} teeTime={selectedTeeTime} closeHandler={handleAddEditClose} />
        }
        { confirmationModalOpen && 
          <DeleteConfirmationModal open={confirmationModalOpen}
            closeHandler={() => closeConfirmationModal()} teeTimeId={selectedTeeTime?.id as number} />
        }
         <Button onClick={onAddEditClick} className='self-end !bg-yellow-400' size='md'>
          Add Tee Time
        </Button>
        <TableContainer>
          <Table variant='simple'>
            {rows?.length ? <TableCaption className="!text-white">All of your tee times!</TableCaption> : null }
            <Thead className='border-b-white border-b-2'>
              <Tr>
                <Th className='!text-xl !text-white'>Time</Th>
                <Th className='!text-xl !text-white'>Players</Th>
                <Th className='!text-xl !text-white'>Course</Th>
              </Tr>
            </Thead>
            <Tbody className='w-full'>
              {
              rows.map(((row: TeeTime) => (
                <Tr className="!text-white" key={row.id}>
                  <Td>{row.time}</Td>
                  <Td>{row.players}</Td>
                  <Td>{row.course}</Td>
                  <Td>
                    <TableActionMenu row={row} handleDelete={onDeleteClicked} handleEdit={onEditClicked} />
                  </Td>
                  
                </Tr>
              )))
            }
            </Tbody>
          </Table>
        </TableContainer>
        {rows?.length ? null :
          <Stack className='w-full'>
            <Skeleton className='w-full mt-5' height="20px" />
            <Skeleton className='w-full' height="20px" />
            <Skeleton className='w-full' height="20px" />
            <Skeleton className='w-full' height="20px" />
            <Skeleton className='w-full' height="20px" />
            <Skeleton className='w-full' height="20px" />
          </Stack>
        }
    </div>
    );
  }