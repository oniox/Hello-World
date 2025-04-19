import os
import pickle
import base64
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request
from googleapiclient.discovery import build
import argparse

# Define the scopes the app needs
# Using modify scope to allow deletion (not just readonly)
SCOPES = ['https://www.googleapis.com/auth/gmail.modify']

def get_gmail_service():
    """
    Authenticates the user and returns a Gmail API service instance.
    """
    creds = None
    # The file token.pickle stores the user's access and refresh tokens
    if os.path.exists('token.pickle'):
        with open('token.pickle', 'rb') as token:
            creds = pickle.load(token)
    
    # If no valid credentials available, let the user log in
    if not creds or not creds.valid:
        if creds and creds.expired and creds.refresh_token:
            creds.refresh(Request())
        else:
            # You need to have credentials.json file downloaded from Google Cloud Console
            flow = InstalledAppFlow.from_client_secrets_file(
                'credentials.json', SCOPES)
            creds = flow.run_local_server(port=0)
            
        # Save the credentials for the next run
        with open('token.pickle', 'wb') as token:
            pickle.dump(creds, token)

    # Return the Gmail API service
    return build('gmail', 'v1', credentials=creds)

def build_query(sender=None, subject=None):
    """
    Builds a Gmail search query based on sender and/or subject
    """
    query_parts = []
    
    if sender:
        query_parts.append(f"from:{sender}")
    
    if subject:
        # Wrap subject in quotes if it contains spaces
        if ' ' in subject:
            query_parts.append(f"subject:\"{subject}\"")
        else:
            query_parts.append(f"subject:{subject}")
    
    return ' '.join(query_parts)

def find_messages(service, user_id='me', query=''):
    """
    Find all messages that match the query
    """
    try:
        # Get list of messages that match the query
        response = service.users().messages().list(userId=user_id, q=query).execute()
        messages = []
        
        if 'messages' in response:
            messages.extend(response['messages'])
        
        # Continue getting messages if there are more pages
        while 'nextPageToken' in response:
            page_token = response['nextPageToken']
            response = service.users().messages().list(
                userId=user_id, q=query, pageToken=page_token).execute()
            if 'messages' in response:
                messages.extend(response['messages'])
        
        return messages
    
    except Exception as e:
        print(f"An error occurred: {e}")
        return []

def delete_messages(service, message_ids, user_id='me'):
    """
    Delete messages with the given IDs
    """
    try:
        # Process in batches to avoid API limits
        batch_size = 100
        total = len(message_ids)
        
        for i in range(0, total, batch_size):
            batch = message_ids[i:i+batch_size]
            service.users().messages().batchDelete(
                userId=user_id,
                body={'ids': batch}
            ).execute()
            print(f"Deleted batch {i//batch_size + 1} ({len(batch)} messages)")
        
        print(f"Successfully deleted all {total} messages")
    
    except Exception as e:
        print(f"An error occurred while deleting: {e}")

def main():
    # Set up argument parser
    parser = argparse.ArgumentParser(description='Delete Gmail messages by sender and/or subject')
    parser.add_argument('--sender', type=str, help='Delete emails from this sender')
    parser.add_argument('--subject', type=str, help='Delete emails with this subject (optional)')
    parser.add_argument('--dry-run', action='store_true', help='Perform a dry run without deleting')
    args = parser.parse_args()
    
    # Ensure at least one filter is provided
    if not args.sender and not args.subject:
        parser.error("At least one of --sender or --subject must be specified")
    
    # Get authenticated Gmail service
    service = get_gmail_service()
    
    # Build search query
    query = build_query(sender=args.sender, subject=args.subject)
    print(f"Searching for emails matching: {query}")
    
    # Find matching messages
    messages = find_messages(service, query=query)
    
    if not messages:
        print("No messages found matching the criteria.")
        return
    
    print(f"Found {len(messages)} messages matching the criteria.")
    
    # Extract just the message IDs
    message_ids = [msg['id'] for msg in messages]
    
    # Delete messages or just show what would be deleted
    if args.dry_run:
        print("DRY RUN - No messages were deleted. Run without --dry-run to delete messages.")
    else:
        confirmation = input(f"Are you sure you want to delete {len(message_ids)} messages? (yes/no): ")
        if confirmation.lower() == 'yes':
            delete_messages(service, message_ids)
        else:
            print("Operation cancelled.")

if __name__ == '__main__':
    main()