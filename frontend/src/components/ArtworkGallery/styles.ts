import styled from 'styled-components'

export const StyledArtworkGallery = styled.ul`
  display: flex;
  align-items: center;
  list-style: none;
  height: 100vh;
  padding: 0;
  margin: 0;
  overflow-x: auto;
  overflow-y: hidden;
  scroll-snap-type: x mandatory;
  overflow-anchor: none;
  background: #c5c5c5;
`
