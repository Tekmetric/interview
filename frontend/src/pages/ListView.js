import React, { useEffect } from 'react';
import styled from 'styled-components';
import Person, { NewPerson } from '../components/Person'
import { usePersons } from '../data/PersonsProvider';

const Container = styled.div`
    max-width: 600px;
    margin: 2em auto;
`;

const ListView = ({ navigate }) => {
    const { persons, list } = usePersons();

    useEffect(() => {
        list()
    }, [list])

    return (
        <Container>
            {Object.keys(persons).map((id) => (
                <Person key={id} id={id} navigate={navigate} />
            ))}
            <NewPerson />
        </Container>
    )
}

export default ListView