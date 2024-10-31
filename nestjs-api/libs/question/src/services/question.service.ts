import { Injectable } from '@nestjs/common';
import { Question as PrismaQuestion } from '@tekmetric/database';
import { CreateQuestionDto } from '../objects/dto/create-question.dto';
import { Question } from '../objects/models/question.model';
import { QuestionRepository } from '../repositories/question.repository';
import { AnswerServices } from './answer.service';

@Injectable()
export class QuestionService {
  constructor(
    private readonly repo: QuestionRepository,
    private readonly answerService: AnswerServices,
  ) {}

  public async getQuestions() {
    const questions = await this.repo.getQuestions();

    if (!questions.length) {
      return [];
    }

    return questions.map(this.mapQuestion);
  }

  public async getQuestionById(id: string) {
    const question = await this.repo.getQuestionById(id);

    if (!question) {
      return null;
    }

    return this.mapQuestion(question);
  }

  public getAuthorById(id: string) {
    return this.repo.getAuthorById(id);
  }

  public async createQuestion(authorId: string, data: CreateQuestionDto) {
    const question = await this.repo.createQuestion(authorId, data);

    if (!question) {
      return null;
    }

    return this.mapQuestion(question);
  }

  public async getAnswersByQuestionId(questionId: string) {
    const answers = await this.repo.getAnswersByQuestionId(questionId);

    if (!answers.length) {
      return [];
    }

    return answers.map(this.answerService.mapAnswer);
  }

  private mapQuestion({
    id,
    authorId,
    title,
    description,
    createdAt,
  }: PrismaQuestion): Question {
    return {
      id,
      authorId,
      title,
      description,
      createdAt: createdAt,
    };
  }
}
