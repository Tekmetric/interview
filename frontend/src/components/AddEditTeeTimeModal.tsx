import { Modal, ModalOverlay, ModalContent, ModalHeader, ModalCloseButton, ModalBody, ModalFooter, Button, Input } from "@chakra-ui/react";
import { TeeTime } from "../interfaces/tee-time.interface";
import { useCallback } from "react";
import axios from "axios";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";

export interface AddEditTeeTimeModalProps { 
    open: boolean;
    teeTime: TeeTime | null;
    closeHandler: () => void;
}

interface AddEditTeeTimeModalForm {
    time: string;
    players: string;
    course: string;
}


export default function AddEditTeeTimeModal({ open, teeTime, closeHandler }: AddEditTeeTimeModalProps ) {
    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm<AddEditTeeTimeModalForm>()

    const onSubmit = useCallback(async (form: AddEditTeeTimeModalForm) => {
        if (teeTime) {
            await axios.put(`http://localhost:8080/tee-times/${teeTime.id}`, form)
        } else {
            await axios.post(`http://localhost:8080/tee-times`, form)
        }

        closeHandler();
        toast.success('Tee Time saved successfully')
    }, [teeTime, closeHandler])

    type fieldKey = keyof Pick<TeeTime, 'time' | 'course' | 'players'>;

    const errorField = <span className="text-red-500 text-sm">This field is required</span>

    const inputField = (placeholder: string, fieldName: keyof Pick<TeeTime, 'time' | 'course' | 'players'>) => (
        <>
        <Input key={fieldName} placeholder={placeholder} defaultValue={teeTime?.[fieldName]} {...register(fieldName, { required: true })} />
        {errors?.[fieldName] && errorField }
        </>
    )

    const fieldMap: Record<fieldKey, string> = {
        'time': 'Tee Time',
        'players': 'Players',
        'course': 'Course'
    }

    return (
       <Modal isOpen={open} onClose={closeHandler}>
        <form onSubmit={handleSubmit(onSubmit)}>
            <ModalOverlay />
            <ModalContent>
            <ModalHeader>{teeTime ? 'Edit Tee Time' : 'Add Tee Time'}</ModalHeader>
            <ModalCloseButton />
            <ModalBody className="flex flex-col justify-center gap-2">
                {
                    Object.keys(fieldMap).map((key) => 
                        inputField(fieldMap[key as fieldKey], key as fieldKey)
                    )
                }
            </ModalBody>

            <ModalFooter>
                <Button type='button' mr={3} onClick={closeHandler}>
                Cancel
                </Button>
                <Button className="!bg-yellow-400" type='submit'>Save</Button>
            </ModalFooter>
            </ModalContent>
        </form>
      </Modal>
    )
}