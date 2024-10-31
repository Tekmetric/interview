import { Injectable } from '@nestjs/common';
import { Answer as PrismaAnswer } from '@tekmetric/database';
import { CreateAnswerDto } from '../objects/dto/create-answer.dto';
import { AnswerRepository } from '../repositories/answer.repository';
import { Answer } from '../objects/models/answer.model';

@Injectable()
export class AnswerServices {
  constructor(private readonly repo: AnswerRepository) {}

  public getAuthorById(id: string) {
    return this.repo.getAuthorById(id);
  }

  public async createAnswer(authorId: string, data: CreateAnswerDto) {
    const answer = await this.repo.createAnswer(authorId, data);

    if (!answer) {
      return null;
    }

    return this.mapAnswer(answer);
  }

  public mapAnswer({
    authorId,
    id,
    description,
    createdAt,
  }: PrismaAnswer): Answer {
    return {
      id,
      authorId,
      description,
      createdAt,
    };
  }
}
