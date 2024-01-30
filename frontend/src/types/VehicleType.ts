type VehicleType = {
    id: number,
    licensePlate: string,
    state: string,
    brand: string,
    model: string,
    registrationYear: number,
    cost: number,
    creationDate: Date
}

export function stateStr(state: string) {
    if (state === 'IN_PROGRESS')
        return 'In Progress';
    if (state === 'PENDING')
        return 'Pending';
    if (state === 'COMPLETED')
        return 'Completed';
    return 'Not Started';
}

export default VehicleType;