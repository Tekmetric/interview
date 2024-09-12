import { useDisclosure, Modal, ModalOverlay, ModalContent, ModalHeader, ModalCloseButton, ModalBody, ModalFooter, Button } from "@chakra-ui/react"
import axios from "axios";
import { useCallback } from "react";
import toast from "react-hot-toast";

export interface DeleteConfirmationModalProps {
  open?: boolean;
  closeHandler: () => void;
  teeTimeId: number;
}

export default function DeleteConfirmationModal({ open = false, closeHandler, teeTimeId }: DeleteConfirmationModalProps) {
  const {  onClose } = useDisclosure()

  const deleteTeeTime = useCallback(async () => {
    await axios.delete(`http://localhost:8080/tee-times/${teeTimeId}`)
    toast.success('Tee Time deleted successfully')
    closeHandler();
  }, [teeTimeId, closeHandler]);
  
  return (
    <>
      <Modal isOpen={open} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Are you sure you want to remove this Tee Time?</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
          </ModalBody>

          <ModalFooter>
            <Button mr={3} onClick={closeHandler}>
              No
            </Button>
            <Button className="!bg-yellow-400" onClick={deleteTeeTime}>Yes</Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </>
  )
}