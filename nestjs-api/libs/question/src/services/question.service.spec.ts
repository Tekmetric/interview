import { Test, TestingModule } from '@nestjs/testing';
import { QuestionService } from './question.service';
import { QuestionRepository } from '../repositories/question.repository';
import { AnswerServices } from './answer.service';
import { CreateQuestionDto } from '../objects/dto/create-question.dto';
import {
  QuestionStatus,
  Question as PrismaQuestion,
  Answer,
} from '@tekmetric/database';
import { Question } from '../objects/models/question.model';

describe('QuestionService', () => {
  let questionService: QuestionService;
  let questionRepository: QuestionRepository;
  let answerService: AnswerServices;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        QuestionService,
        {
          provide: QuestionRepository,
          useValue: {
            getQuestionsByStatus: jest.fn(),
            getQuestionById: jest.fn(),
            getAuthorById: jest.fn(),
            createQuestion: jest.fn(),
            resolveQuestion: jest.fn(),
            getAnswersByQuestionId: jest.fn(),
          },
        },
        {
          provide: AnswerServices,
          useValue: {
            mapAnswer: jest.fn(),
          },
        },
      ],
    }).compile();

    questionService = module.get<QuestionService>(QuestionService);
    questionRepository = module.get<QuestionRepository>(QuestionRepository);
    answerService = module.get<AnswerServices>(AnswerServices);
  });

  describe('getQuestionsByStatus', () => {
    it('should return an array of questions', async () => {
      const status = QuestionStatus.PENDING;
      const questions: PrismaQuestion[] = [
        {
          id: '1',
          authorId: '1',
          title: 'Question 1',
          description: 'Description 1',
          createdAt: new Date(),
          status,
          updatedAt: undefined,
        },
      ];

      jest
        .spyOn(questionRepository, 'getQuestionsByStatus')
        .mockResolvedValue(questions);

      const result = await questionService.getQuestionsByStatus(status);

      expect(questionRepository.getQuestionsByStatus).toHaveBeenCalledWith(
        status,
      );
      expect(result).toEqual(questions.map(questionService['mapQuestion']));
    });

    it('should return an empty array if no questions found', async () => {
      const status = QuestionStatus.PENDING;

      jest
        .spyOn(questionRepository, 'getQuestionsByStatus')
        .mockResolvedValue([]);

      const result = await questionService.getQuestionsByStatus(status);

      expect(questionRepository.getQuestionsByStatus).toHaveBeenCalledWith(
        status,
      );
      expect(result).toEqual([]);
    });
  });

  describe('getQuestionById', () => {
    it('should return a question', async () => {
      const id = '1';
      const question: PrismaQuestion = {
        id,
        authorId: '1',
        title: 'Question 1',
        description: 'Description 1',
        createdAt: new Date(),
        status: QuestionStatus.PENDING,
        updatedAt: undefined,
      };

      jest
        .spyOn(questionRepository, 'getQuestionById')
        .mockResolvedValue(question);

      const result = await questionService.getQuestionById(id);

      expect(questionRepository.getQuestionById).toHaveBeenCalledWith(id);
      expect(result).toEqual(questionService['mapQuestion'](question));
    });

    it('should return null if question not found', async () => {
      const id = '1';

      jest.spyOn(questionRepository, 'getQuestionById').mockResolvedValue(null);

      const result = await questionService.getQuestionById(id);

      expect(questionRepository.getQuestionById).toHaveBeenCalledWith(id);
      expect(result).toBeNull();
    });
  });

  describe('createQuestion', () => {
    it('should create and return a question', async () => {
      const authorId = '1';
      const data: CreateQuestionDto = {
        title: 'Question 1',
        description: 'Description 1',
      };
      const question: PrismaQuestion = {
        id: '1',
        authorId,
        title: data.title,
        description: data.description,
        createdAt: new Date(),
        status: QuestionStatus.PENDING,
        updatedAt: undefined,
      };

      jest
        .spyOn(questionRepository, 'createQuestion')
        .mockResolvedValue(question);

      const result = await questionService.createQuestion(authorId, data);

      expect(questionRepository.createQuestion).toHaveBeenCalledWith(
        authorId,
        data,
      );
      expect(result).toEqual(questionService['mapQuestion'](question));
    });

    it('should return null if question creation failed', async () => {
      const authorId = '1';
      const data: CreateQuestionDto = {
        title: 'Question 1',
        description: 'Description 1',
      };

      jest.spyOn(questionRepository, 'createQuestion').mockResolvedValue(null);

      const result = await questionService.createQuestion(authorId, data);

      expect(questionRepository.createQuestion).toHaveBeenCalledWith(
        authorId,
        data,
      );
      expect(result).toBeNull();
    });
  });

  describe('resolveQuestion', () => {
    it('should resolve and return a question', async () => {
      const id = '1';
      const question: PrismaQuestion = {
        id,
        authorId: '1',
        title: 'Question 1',
        description: 'Description 1',
        createdAt: new Date(),
        status: QuestionStatus.COMPLETED,
        updatedAt: undefined,
      };

      jest
        .spyOn(questionRepository, 'resolveQuestion')
        .mockResolvedValue(question);

      const result = await questionService.resolveQuestion(id);

      expect(questionRepository.resolveQuestion).toHaveBeenCalledWith(id);
      expect(result).toEqual(questionService['mapQuestion'](question));
    });

    it('should return null if question resolution failed', async () => {
      const id = '1';

      jest.spyOn(questionRepository, 'resolveQuestion').mockResolvedValue(null);

      const result = await questionService.resolveQuestion(id);

      expect(questionRepository.resolveQuestion).toHaveBeenCalledWith(id);
      expect(result).toBeNull();
    });
  });

  describe('getAnswersByQuestionId', () => {
    it('should return an array of answers', async () => {
      const questionId = '1';
      const answers: Answer[] = [
        {
          id: '1',
          questionId,
          description: 'Answer 1',
          createdAt: new Date(),
          updatedAt: new Date(),
          authorId: '1',
        },
      ];

      jest
        .spyOn(questionRepository, 'getAnswersByQuestionId')
        .mockResolvedValue(answers);
      jest
        .spyOn(answerService, 'mapAnswer')
        .mockImplementation((answer) => answer);

      const result = await questionService.getAnswersByQuestionId(questionId);

      expect(questionRepository.getAnswersByQuestionId).toHaveBeenCalledWith(
        questionId,
      );
      expect(result).toEqual(answers);
    });

    it('should return an empty array if no answers found', async () => {
      const questionId = '1';

      jest
        .spyOn(questionRepository, 'getAnswersByQuestionId')
        .mockResolvedValue([]);

      const result = await questionService.getAnswersByQuestionId(questionId);

      expect(questionRepository.getAnswersByQuestionId).toHaveBeenCalledWith(
        questionId,
      );
      expect(result).toEqual([]);
    });
  });

  describe('getShortDescription', () => {
    it('should return a short description', async () => {
      const question: Question = {
        id: '1',
        authorId: '1',
        title: 'Question 1',
        description: `Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent venenatis eu nunc quis interdum. Duis venenatis velit dui, cursus euismod purus feugiat nec. Fusce pharetra ullamcorper diam. Maecenas eu nulla elit. Proin neque augue, tempor in lorem nec, malesuada luctus ante. Curabitur posuere quam augue, sed ullamcorper lorem dictum vitae. Fusce sed rhoncus justo. Quisque scelerisque leo eu rutrum sollicitudin. Cras lobortis nunc quam, ut semper metus scelerisque quis. Nullam malesuada turpis ut ultricies mollis. Maecenas cursus libero nec fermentum congue. Fusce tempus ex sit amet urna congue, sit amet pharetra leo consequat. Nullam dignissim leo ac lobortis dignissim. Maecenas libero sapien, ultricies ullamcorper blandit sed, varius non enim. Integer interdum velit in semper sodales. Ut ut mauris vel purus molestie posuere ac eu sapien.`,
        createdAt: new Date(),
        status: QuestionStatus.PENDING,
      };

      const result = await questionService.getShortDescription(question);

      expect(result).toEqual(
        `Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent venenatis eu nunc quis interdum. Duis venenatis velit dui, cursus euismod purus feugiat nec. Fusce pharetra ullamcorper diam. Maecenas eu nulla elit. Proin neque augue, tempor in lorem nec, malesuada luctus ante. Curabitur posuere quam augue, sed ullamcorper lorem dictum vitae.`,
      );
    });

    it('should return an empty string if description is not provided', async () => {
      const question: Question = {
        id: '1',
        authorId: '1',
        title: 'Question 1',
        description: '',
        createdAt: new Date(),
        status: QuestionStatus.PENDING,
      };

      const result = await questionService.getShortDescription(question);

      expect(result).toEqual('');
    });
  });
});
