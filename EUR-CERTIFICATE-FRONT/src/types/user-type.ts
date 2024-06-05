
export default interface IUser {
    id?: any | null,
    username: string,
    password: string,
    roles?: Array<string>
}