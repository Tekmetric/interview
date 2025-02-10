import type { RepoData } from '@customTypes/api/github';

export async function getRepoData(): Promise<RepoData> {
  const response = await fetch(
    'https://api.github.com/repos/jdk2pq/Tekmetric-interview'
  );
  return await response.json();
}
