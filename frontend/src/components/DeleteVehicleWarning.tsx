import Modal from 'react-bootstrap/Modal';
import Button from 'react-bootstrap/Button';

interface DeleteVehicleWarningProps {
    vehicleId: number | null;
    onDelete: Function
}

export default function DeleteVehicleWarning(props: DeleteVehicleWarningProps) {

    return (
        <Modal show={!!props.vehicleId} onHide={() => props.onDelete(props.vehicleId, true)}>
            <Modal.Body>
                You are about to delete a vehicle.<br /><br /> Do you want to continue?
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={() => props.onDelete(props.vehicleId, true)}>
                    Cancel
                </Button>
                <Button variant="danger" onClick={() => props.onDelete(props.vehicleId)}>
                    Delete
                </Button>
            </Modal.Footer>
        </Modal>
    );
}