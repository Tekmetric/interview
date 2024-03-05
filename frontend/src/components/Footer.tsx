import Link from '@mui/material/Link';

import GitHubIcon from '@mui/icons-material/GitHub';
import LinkedInIcon from '@mui/icons-material/LinkedIn';

const Footer = () => (
  <div className="flex flex-col gap-y-3 justify-center items-center w-screen bg-black max-h-fit py-6">
    <div className="text-white text-xl">Radu Baston</div>
    <div
      data-testid="social-media-links"
      className="flex gap-x-5 justify-center item-center"
    >
      <Link href="https://github.com/radu2147">
        <GitHubIcon sx={{ color: 'white' }} />
      </Link>
      <Link href="https://www.linkedin.com/in/radu-baston-a99960185/">
        <LinkedInIcon sx={{ color: 'white' }} />
      </Link>
    </div>
  </div>
);

export default Footer;
