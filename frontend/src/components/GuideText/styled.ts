import styled from 'styled-components';

export const StyledGuideTextWrapper = styled.div`
    box-sizing: border-box;
    position: absolute;
    z-index: 2;
    top: 0;
    left: 0;
    right: 0;
    min-height: 5vw;
    padding: 0.5vh;
    display: flex;
    justify-content: center;
    align-items: center;
    backdrop-filter: blur(10px);
`

export const StyledGuideText = styled.h1`
    font-style: italic;
    font-size: 1.25rem;
    font-weight: 500;
`