import styled from 'styled-components';

export const StyledArtworkFrameGlass = styled.div`
    position: absolute;
    z-index: 1;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    background-image: url('/room.png');
    background-size: cover;
    opacity: 0.02;
    transform: scale(1.5);
    pointer-events: none;
`