import styled, { css } from 'styled-components';

const variables = css`
    --inner-frame-width: 40px;
    --outer-frame-width: 25px;
    --frame-width: calc(var(--inner-frame-width) + var(--outer-frame-width));
`

export const StyledArtworkImageSpace = styled.li`
    position: relative;
    display: flex;
    flex-shrink: 0;
    justify-content: center;
    align-items: center;
    width: 100vw;
    height: 100%;
    scroll-snap-align: start;
`

export const StyledArtworkImageFrame = styled.div`
    ${variables}

    position: relative;
    display: block;
    margin: var(--frame-width);
    box-shadow: 0 0 0 var(--inner-frame-width) #f1efef;
    z-index: 1;

    &:before {
        content: '';
        position: absolute;
        z-index: 1;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        box-shadow: inset 0 6px 6px 2px rgba(0, 0, 0, 0.2);
    }

    &:after {
        content: '';
        position: absolute;
        z-index: 1;
        top: calc(var(--frame-width) * -1);
        left: calc(var(--frame-width) * -1);
        right: calc(var(--frame-width) * -1);
        bottom: calc(var(--frame-width) * -1);
        border: var(--outer-frame-width) solid #e1dede;
        box-shadow: inset 0 6px 6px 4px rgba(0, 0, 0, 0.3),
        8px 10px 10px -5px rgba(0, 0, 0, 0.25),
        10px 15px 30px -5px rgba(0, 0, 0, 0.25);
    }
`

export const StyledArtworkImage = styled.img<{
  blurDataUrl: string
}>`
    ${variables}
    
    max-width: calc(75vw - var(--frame-width));
    max-height: calc(75vh - var(--frame-width));
    position: relative;
    background: #f0f0f0;

    ${({ blurDataUrl }) => css`
        background-image: url(${blurDataUrl});
        background-repeat: no-repeat;
        background-position: center;
    `}
`

export const StyledLightOverlay = styled.div`
    position: absolute;
    width: 100%;
    height: 100%;
    top: 0;
    left: 0;
    background: rgb(240, 244, 244);
    background: radial-gradient(circle at 50% 0, rgb(238, 238, 238) 4%, rgb(197, 197, 197) 50%);
    pointer-events: all;
`