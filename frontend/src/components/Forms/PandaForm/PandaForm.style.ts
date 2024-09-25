import { styled } from "@mui/material";

export const ColourPickerIcon = styled('span')(({ theme }) => ({
  borderRadius: '50%',
  width: 42,
  height: 42,
  'input:hover ~ &': {
    backgroundColor: '#ebf1f5',
    ...theme.applyStyles('dark', {
      backgroundColor: '#30404d',
    }),
  },
}));

export const ColourPickerIconChecked = styled(ColourPickerIcon)({
  '&::before': {
    display: 'block',
    width: 42,
    height: 42,
    backgroundImage: 'radial-gradient(#fff,#fff 36%,transparent 42%)',
    content: '""',
  },
});
