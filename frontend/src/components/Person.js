import React, { useCallback, useEffect, useState } from 'react';
import styled from 'styled-components'
import { usePersons } from '../data/PersonsProvider';

const Container = styled.div`
    background-color: #eee;
    padding: 1em 2em;
    margin-bottom: 1em;
    position: relative;
    display: flex;
    justify-content: space-between;
    gap: 0.5em;
`;

const ID = styled.div`
    font-size: 0.6em;
    color: #666;
`;

const Name = styled.input`
    background-color: inherit;
    border: none;

    &:focus {
        outline: none;
    }
`;

const Delete = styled.button`
    background-color: rgba(255,0,0,0.2);
    border: 1px solid #c00;
    color: #c00;
    cursor: pointer;
`;

const Add = styled.button`
    background-color: rgba(0,255,0,0.2);
    border: 1px solid #0c0;
    color: #0c0;
    cursor: pointer;
`;

const View = styled.button`
    background-color: rgba(0,0,255,0.2);
    border: 1px solid #00c;
    color: #00c;
    cursor: pointer;
`;

const Content = styled.div`
    flex-grow: 1;
`;

export const NewPerson = () => {
    const { create } = usePersons();
    const [name, setName] = useState('')

    const onChange = useCallback((event) => {
        setName(event.currentTarget.value)
    }, [setName]);

    const onCreate = useCallback(() => {
        create({ name })
        setName('')
    }, [create, name])

    return (
        <Container>
            <Name value={name} onChange={onChange} />
            <Add onClick={onCreate}>Add</Add>
        </Container>
    )
}

const Person = ({ id, navigate }) => {
    const { persons, update, remove, get } = usePersons();

    const onChange = useCallback((event) => {
        update(id, { id, name: event.currentTarget.value })
    }, [update, id]);

    const onRemove = useCallback(() => {
        remove(id)
    }, [remove, id]);

    useEffect(() => {
        if (!persons[id]) {
            get(id)
        }
    }, [persons, id, get])

    if (!persons[id]) return null

    return (
        <Container>
            <Content>
                <Name value={persons[id].name} onChange={onChange} />
                <ID>{id}</ID>
            </Content>
            {navigate && <View onClick={() => navigate(id)}>View</View>}
            {navigate && <Delete onClick={onRemove}>Delete</Delete>}
        </Container>
    )
}

export default Person