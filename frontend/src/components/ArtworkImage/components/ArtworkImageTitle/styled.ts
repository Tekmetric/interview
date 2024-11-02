import styled from 'styled-components'

import { artworkImageVariables } from '../../styled'

export const StyledArtworkImageTitle = styled.div`
  ${artworkImageVariables};

  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  box-sizing: border-box;
  min-height: var(--inner-frame-width);
  width: 0;
  min-width: 100%;
  padding: 5px 0;
  font-style: italic;
  line-height: 1.5rem;
`
