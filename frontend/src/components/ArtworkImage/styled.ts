import styled, { css } from 'styled-components';

const variables = css`
    --inner-frame-width: 40px;
    --outer-frame-width: 25px;
    --frame-width: calc(var(--inner-frame-width) + var(--outer-frame-width));
`

export const StyledArtworkImageOuterFrame = styled.div`
    ${variables};

    position: relative;
    display: block;
    box-sizing: border-box;
    padding: var(--outer-frame-width);
    z-index: 1;
    background: #e1dede;

    &:after {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        z-index: 1;
        box-shadow: 8px 10px 10px -5px rgba(124, 112, 103, 0.35),
        0 30px 10px -15px rgba(124, 112, 103, 0.35);
        pointer-events: none;
    }
`

const getOuterFrameBackgroundSharedStyles = (position: 'top' | 'bottom') => css`
    ${variables};

    position: absolute;
    z-index: 2;
    ${position}: 0;
    left: 0;
    right: 0;
    height: var(--outer-frame-width);
`

const getOuterFrameBackgroundPseudoSharedStyles = (position: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right') => {
    const [vertical, horizontal] = position.split('-') as ['top' | 'bottom', 'left' | 'right']

    return css`
        ${variables};
        
        --gap-color: rgba(166, 161, 161, 0.3);
        --gap-start: 49.95%;
        --gap-end: 50.05%;
    
        content: '';
        position: absolute;
        ${vertical}: 0;
        ${horizontal}: 0;
        width: var(--outer-frame-width);
        height: var(--outer-frame-width);
        background-image: 
            linear-gradient(
                to ${vertical === 'top' ? 'bottom' : 'top'} ${horizontal}, 
                transparent var(--gap-start), 
                 var(--gap-color) var(--gap-start), 
                 var(--gap-color) var(--gap-end), 
                transparent var(--gap-end)
            );
    `
}

export const StyledArtworkImageOuterFrameBackgroundTop = styled.div`
    ${getOuterFrameBackgroundSharedStyles('top')}
    
    &:before {
        ${getOuterFrameBackgroundPseudoSharedStyles('top-left')}
    }

    &:after {
        ${getOuterFrameBackgroundPseudoSharedStyles('top-right')}
    }
`

export const StyledArtworkImageOuterFrameBackgroundBottom = styled.div`
    ${getOuterFrameBackgroundSharedStyles('bottom')}
    
    &:before {
        ${getOuterFrameBackgroundPseudoSharedStyles('bottom-left')}
    }

    &:after {
        ${getOuterFrameBackgroundPseudoSharedStyles('bottom-right')}
    }
`

export const StyledArtworkImageFrame = styled.div`
    ${variables};
    
    box-sizing: border-box;
    padding: var(--inner-frame-width) var(--inner-frame-width) 0 var(--inner-frame-width);
    background: #f1efef;
    box-shadow: inset 0 4px 6px 0px rgba(0, 0, 0, 0.3);
`

export const StyledArtworkImageFrameShadow = styled.div`
    ${variables};
    
    position: relative;

    &:after {
        content: '';
        position: absolute;
        z-index: 1;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        box-shadow: inset 0 2px 6px 2px rgba(0, 0, 0, 0.2);
        pointer-events: none;
    }
`

export const StyledArtworkImage = styled.img<{
  $blurDataUrl: string
}>`
    ${variables};
    
    position: relative;
    
    max-width: calc(75vw - var(--frame-width));
    max-height: calc(75vh - var(--frame-width));
    background: #f0f0f0;

    ${({ $blurDataUrl }) => css`
        background-image: url(${$blurDataUrl});
        background-repeat: no-repeat;
        background-size: cover;
        background-position: center;
    `}
`

export const StyledArtworkImageTitle = styled.div`
    ${variables};
    
    display: flex;
    justify-content: center;
    align-items: center;
    box-sizing: border-box;
    min-height: var(--inner-frame-width);
    width: 0;
    min-width: 100%;
    padding: 5px 0;
    font-style: italic;
    line-height: 1.5rem;
`
