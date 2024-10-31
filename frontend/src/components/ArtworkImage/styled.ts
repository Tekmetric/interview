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

const getFrameBackgroundSharedStyles = (position: 'top' | 'bottom') => css`
    ${variables}

    position: absolute;
    z-index: 2;
    ${position}: calc(var(--frame-width) * -1);
    left: calc(var(--frame-width) * -1);
    right: calc(var(--frame-width) * -1);
    height: var(--frame-width);
`

const getFrameBackgroundPseudoSharedStyles = (position: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right') => {
    const [vertical, horizontal] = position.split('-') as ['top' | 'bottom', 'left' | 'right']

    return css`
        ${variables}
    
        content: '';
        position: absolute;
        ${vertical}: 0;
        ${horizontal}: 0;
        width: var(--outer-frame-width);
        height: var(--outer-frame-width);
        background-image: linear-gradient(to ${vertical === 'top' ? 'bottom' : 'top'} ${horizontal}, transparent 49.95%, rgba(166, 161, 161, 0.2) 49.95%, rgba(166, 161, 161, 0.18) 50.05%, transparent 50.05%);
    `
}

export const StyledArtworkImageFrameBackgroundTop = styled.div`
    ${getFrameBackgroundSharedStyles('top')}
    
    &:before {
        ${getFrameBackgroundPseudoSharedStyles('top-left')}
    }

    &:after {
        ${getFrameBackgroundPseudoSharedStyles('top-right')}
    }
`

export const StyledArtworkImageFrameBackgroundBottom = styled.div`
    ${getFrameBackgroundSharedStyles('bottom')}
    
    &:before {
        ${getFrameBackgroundPseudoSharedStyles('bottom-left')}
    }

    &:after {
        ${getFrameBackgroundPseudoSharedStyles('bottom-right')}
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