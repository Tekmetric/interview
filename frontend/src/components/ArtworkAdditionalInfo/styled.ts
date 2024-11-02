import styled, { css } from 'styled-components';

export const StyledArtworkAdditionalInfo = styled.div<{
  $visible: boolean
}>`
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    padding: 10%;
    display: flex;
    flex-direction: column;
    overflow-y: auto;
    background: rgba(0, 0, 0, 0.5);
    backdrop-filter: blur(20px);
    transition: opacity .3s;
    color: var(--color-white);
    line-height: 2rem;
    opacity: 0;
    pointer-events: none;

    ${({ $visible }) => $visible && css`
        pointer-events: all;
        opacity: 1;
    `}
`
