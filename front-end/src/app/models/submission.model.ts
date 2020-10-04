export class SubmissionModel {
  id: number;
  expiryDate: Date;
  releaseDate: Date;
  content: string;
  history: any[];
  expiryString: string;
  releaseString: string;
  isRevisable: boolean;
}
