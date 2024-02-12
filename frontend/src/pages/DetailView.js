import React from 'react';
import styled from 'styled-components';
import Person from '../components/Person'

const Container = styled.div`
    max-width: 600px;
    margin: 2em auto;
`;

const BackButton = styled.button`
    background: inherit;
    border: none;
    cursor: pointer;
    margin-bottom: 1em;
    
    &:hover {
        border-left: 2px solid;
    }
`;

const DetailView = ({ id, prev }) => (
    <Container>
        <BackButton onClick={prev}>Back</BackButton>
        <Person id={id} />
    </Container>
)

export default DetailView